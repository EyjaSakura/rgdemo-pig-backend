package com.pig4cloud.pig.homework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.admin.api.entity.SysDept;
import com.pig4cloud.pig.admin.mapper.SysDeptMapper;
import com.pig4cloud.pig.common.core.exception.CheckedException;
import com.pig4cloud.pig.homework.entity.BizCollege;
import com.pig4cloud.pig.homework.mapper.BizCollegeMapper;
import com.pig4cloud.pig.homework.mapper.BizCourseMapper;
import com.pig4cloud.pig.homework.mapper.BizTeachingMapper;
import com.pig4cloud.pig.homework.service.BizCollegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 学院 Service 实现类
 *
 * @author EyjaSakura
 */
@Service
@RequiredArgsConstructor
public class BizCollegeServiceImpl extends ServiceImpl<BizCollegeMapper, BizCollege>
		implements BizCollegeService {

	private final BizCourseMapper courseMapper;
	private final BizTeachingMapper teachingMapper;
	private final SysDeptMapper sysDeptMapper;

	@Override
	public List<BizCollege> listAll() {
		return list(Wrappers.<BizCollege>lambdaQuery()
				.eq(BizCollege::getDelFlag, "0")
				.orderByAsc(BizCollege::getCollegeCode));
	}

	@Override
	public IPage<BizCollege> pageCollege(Page page, String collegeName) {
		LambdaQueryWrapper<BizCollege> wrapper = Wrappers.<BizCollege>lambdaQuery()
				.eq(BizCollege::getDelFlag, "0")
				.like(collegeName != null, BizCollege::getCollegeName, collegeName)
				.orderByAsc(BizCollege::getCollegeCode);
		return this.page(page, wrapper);
	}

	@Override
	public Boolean addCollege(BizCollege college) {
		// 校验编号唯一
		long count = this.count(Wrappers.<BizCollege>lambdaQuery()
				.eq(BizCollege::getCollegeCode, college.getCollegeCode())
				.eq(BizCollege::getDelFlag, "0"));
		if (count > 0) {
			throw new CheckedException("学院编号 [" + college.getCollegeCode() + "] 已存在");
		}

		// 如果传了 deptId，校验该 deptId 在 sys_dept 中确实存在
		if (college.getDeptId() != null) {
			SysDept dept = sysDeptMapper.selectById(college.getDeptId());
			if (dept == null) {
				throw new CheckedException("关联的部门节点不存在（deptId=" + college.getDeptId() + "）");
			}
			// 自动用 sys_dept 的名称填充 collegeName（如果前端没传）
			if (college.getCollegeName() == null || college.getCollegeName().isEmpty()) {
				college.setCollegeName(dept.getName());
			}
		}

		// 如果没传 deptId，尝试在"教学单位"下按名称查找匹配的学院节点
		if (college.getDeptId() == null && college.getCollegeName() != null) {
			// 先找到教学单位
			SysDept teachingUnit = sysDeptMapper.selectOne(
					Wrappers.<SysDept>lambdaQuery()
							.eq(SysDept::getDeptCategory, "1")
							.like(SysDept::getName, "教学单位")
							.last("LIMIT 1"));
			if (teachingUnit != null) {
				SysDept matched = sysDeptMapper.selectOne(
						Wrappers.<SysDept>lambdaQuery()
								.eq(SysDept::getName, college.getCollegeName())
								.eq(SysDept::getDeptCategory, "2")
								.eq(SysDept::getParentId, teachingUnit.getDeptId())
								.last("LIMIT 1"));
				if (matched != null) {
					college.setDeptId(matched.getDeptId());
				}
			}
		}

		// 自动生成 collegeCode（如果没传）
		if (college.getCollegeCode() == null || college.getCollegeCode().isEmpty()) {
			college.setCollegeCode("D" + System.currentTimeMillis());
		}

		college.setCreateTime(new Date());
		college.setUpdateTime(new Date());
		college.setDelFlag("0");
		return this.save(college);
	}

	@Override
	public Boolean updateCollege(BizCollege college) {
		BizCollege existing = this.getById(college.getCollegeId());
		if (existing == null || "1".equals(existing.getDelFlag())) {
			throw new CheckedException("学院不存在");
		}
		// 如果修改了编号，校验唯一
		if (college.getCollegeCode() != null && !college.getCollegeCode().equals(existing.getCollegeCode())) {
			long count = this.count(Wrappers.<BizCollege>lambdaQuery()
					.eq(BizCollege::getCollegeCode, college.getCollegeCode())
					.eq(BizCollege::getDelFlag, "0")
					.ne(BizCollege::getCollegeId, college.getCollegeId()));
			if (count > 0) {
				throw new CheckedException("学院编号 [" + college.getCollegeCode() + "] 已被占用");
			}
		}
		college.setUpdateTime(new Date());
		return this.updateById(college);
	}

	@Override
	public Boolean removeCollege(Long collegeId) {
		BizCollege existing = this.getById(collegeId);
		if (existing == null || "1".equals(existing.getDelFlag())) {
			throw new CheckedException("学院不存在");
		}
		// 检查是否有关联课程
		long courseCount = courseMapper.selectCount(Wrappers.<com.pig4cloud.pig.homework.entity.BizCourse>lambdaQuery()
				.eq(com.pig4cloud.pig.homework.entity.BizCourse::getCollegeId, collegeId)
				.eq(com.pig4cloud.pig.homework.entity.BizCourse::getDelFlag, "0"));
		if (courseCount > 0) {
			throw new CheckedException("该学院下还有关联课程，无法删除");
		}
		// 逻辑删除
		existing.setDelFlag("1");
		existing.setUpdateTime(new Date());
		return this.updateById(existing);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> syncFromDept() {
		int added = 0;
		int updated = 0;
		int skipped = 0;

		// 1. 查询所有已存在的学院
		List<BizCollege> existingColleges = this.list(Wrappers.<BizCollege>lambdaQuery()
				.eq(BizCollege::getDelFlag, "0"));
		Map<Long, BizCollege> existingByDeptId = new HashMap<>();
		for (BizCollege c : existingColleges) {
			if (c.getDeptId() != null) {
				existingByDeptId.put(c.getDeptId(), c);
			}
		}

		// 2. 先查找"教学单位"节点的 deptId（dept_category=1 且名称包含"教学单位"）
		SysDept teachingUnit = sysDeptMapper.selectOne(
				Wrappers.<SysDept>lambdaQuery()
						.eq(SysDept::getDeptCategory, "1")
						.like(SysDept::getName, "教学单位")
						.last("LIMIT 1"));
		if (teachingUnit == null) {
			throw new CheckedException("未在 sys_dept 中找到\"教学单位\"节点，请先在部门管理中创建");
		}

		// 3. 查询"教学单位"下 dept_category = 2 的子节点（真正的学院）
		List<SysDept> deptColleges = sysDeptMapper.selectList(
				Wrappers.<SysDept>lambdaQuery()
						.eq(SysDept::getDeptCategory, "2")
						.eq(SysDept::getParentId, teachingUnit.getDeptId())
						.orderByAsc(SysDept::getSortOrder));

		Date now = new Date();
		for (SysDept dept : deptColleges) {
			BizCollege existing = existingByDeptId.get(dept.getDeptId());
			if (existing != null) {
				// 已通过 deptId 关联的，更新名称（保持同步）
				if (!dept.getName().equals(existing.getCollegeName())) {
					existing.setCollegeName(dept.getName());
					existing.setUpdateTime(now);
					this.updateById(existing);
					updated++;
				} else {
					skipped++;
				}
			} else {
				// 检查是否有同名的已存在学院（可能之前没关联 deptId）
				BizCollege sameName = null;
				for (BizCollege c : existingColleges) {
					if (dept.getName().equals(c.getCollegeName())) {
						sameName = c;
						break;
					}
				}
				if (sameName != null) {
					// 关联 deptId 到已有记录
					sameName.setDeptId(dept.getDeptId());
					sameName.setUpdateTime(now);
					this.updateById(sameName);
					existingByDeptId.put(dept.getDeptId(), sameName);
					updated++;
				} else {
					// 新增学院记录
					BizCollege newCollege = BizCollege.builder()
							.collegeCode("D" + dept.getDeptId())
							.collegeName(dept.getName())
							.deptId(dept.getDeptId())
							.createTime(now)
							.updateTime(now)
							.delFlag("0")
							.build();
					this.save(newCollege);
					added++;
				}
			}
		}

		Map<String, Object> result = new LinkedHashMap<>();
		result.put("total", deptColleges.size());
		result.put("added", added);
		result.put("updated", updated);
		result.put("skipped", skipped);
		return result;
	}
}
