package com.pig4cloud.pig.homework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.common.core.exception.CheckedException;
import com.pig4cloud.pig.admin.api.entity.SysDept;
import com.pig4cloud.pig.admin.mapper.SysDeptMapper;
import com.pig4cloud.pig.homework.entity.BizCollege;
import com.pig4cloud.pig.homework.entity.BizCourse;
import com.pig4cloud.pig.homework.entity.BizTeaching;
import com.pig4cloud.pig.homework.mapper.BizCollegeMapper;
import com.pig4cloud.pig.homework.mapper.BizCourseMapper;
import com.pig4cloud.pig.homework.mapper.BizTeachingMapper;
import com.pig4cloud.pig.homework.service.BizCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 课程 Service 实现类
 *
 * @author EyjaSakura
 */
@Service
@RequiredArgsConstructor
public class BizCourseServiceImpl extends ServiceImpl<BizCourseMapper, BizCourse>
		implements BizCourseService {

	private final BizTeachingMapper teachingMapper;
	private final BizCollegeMapper collegeMapper;
	private final SysDeptMapper sysDeptMapper;

	@Override
	public List<Map<String, Object>> courseTree(String courseName) {
		// 1. 查询 biz_college 中所有未删除的学院
		List<BizCollege> colleges = collegeMapper.selectList(
				Wrappers.<BizCollege>lambdaQuery()
						.eq(BizCollege::getDelFlag, "0")
						.orderByAsc(BizCollege::getCollegeId));

		// 2. 建立 deptId → BizCollege 的映射
		Map<Long, BizCollege> collegeByDeptId = new LinkedHashMap<>();
		for (BizCollege c : colleges) {
			if (c.getDeptId() != null) {
				collegeByDeptId.put(c.getDeptId(), c);
			}
		}

		// 3. 查找"教学单位"节点，并获取其下 dept_category=2 的学院节点
		SysDept teachingUnit = sysDeptMapper.selectOne(
				Wrappers.<SysDept>lambdaQuery()
						.eq(SysDept::getDeptCategory, "1")
						.like(SysDept::getName, "教学单位")
						.last("LIMIT 1"));
		List<SysDept> deptColleges;
		if (teachingUnit != null) {
			deptColleges = sysDeptMapper.selectList(
					Wrappers.<SysDept>lambdaQuery()
							.eq(SysDept::getDeptCategory, "2")
							.eq(SysDept::getParentId, teachingUnit.getDeptId())
							.orderByAsc(SysDept::getSortOrder));
		} else {
			deptColleges = Collections.emptyList();
		}

		// 4. 查询所有未删除的课程（支持按名称模糊搜索）
		LambdaQueryWrapper<BizCourse> courseWrapper = Wrappers.<BizCourse>lambdaQuery()
				.eq(BizCourse::getDelFlag, "0")
				.orderByAsc(BizCourse::getCourseCode);
		if (courseName != null && !courseName.trim().isEmpty()) {
			courseWrapper.like(BizCourse::getCourseName, courseName.trim());
		}
		List<BizCourse> courses = this.list(courseWrapper);

		// 5. 建立 collegeId → 课程列表 的映射
		Map<Long, List<Map<String, Object>>> collegeCourseMap = new LinkedHashMap<>();
		for (BizCourse course : courses) {
			Map<String, Object> courseNode = new LinkedHashMap<>();
			courseNode.put("id", course.getCourseCode());
			courseNode.put("name", course.getCourseCode() + " - " + course.getCourseName());
			collegeCourseMap.computeIfAbsent(course.getCollegeId(), k -> new ArrayList<>()).add(courseNode);
		}

		// 6. 以 sys_dept 为主构建树（保证和部门树完全一致）
		List<Map<String, Object>> tree = new ArrayList<>();
		Set<Long> usedCollegeIds = new HashSet<>();

		for (SysDept dept : deptColleges) {
			BizCollege college = collegeByDeptId.get(dept.getDeptId());
			Map<String, Object> collegeNode = new LinkedHashMap<>();
			collegeNode.put("id", "college_" + dept.getDeptId());
			collegeNode.put("name", dept.getName());
			collegeNode.put("deptId", dept.getDeptId());

			if (college != null) {
				collegeNode.put("collegeId", college.getCollegeId());
				collegeNode.put("children", collegeCourseMap.getOrDefault(college.getCollegeId(), Collections.emptyList()));
				usedCollegeIds.add(college.getCollegeId());
			} else {
				// 在 sys_dept 中有但 biz_college 中还没有对应的记录
				collegeNode.put("collegeId", null);
				collegeNode.put("children", Collections.emptyList());
			}
			tree.add(collegeNode);
		}

		// 7. 把 biz_college 中有但 sys_dept 中没有的也加进来（兜底）
		for (BizCollege college : colleges) {
			if (!usedCollegeIds.contains(college.getCollegeId())) {
				Map<String, Object> collegeNode = new LinkedHashMap<>();
				collegeNode.put("id", "college_" + college.getCollegeId());
				collegeNode.put("name", college.getCollegeName());
				collegeNode.put("collegeId", college.getCollegeId());
				collegeNode.put("children", collegeCourseMap.getOrDefault(college.getCollegeId(), Collections.emptyList()));
				tree.add(collegeNode);
			}
		}

		return tree;
	}

	@Override
	public List<BizCourse> listCourses(Long collegeId) {
		return list(Wrappers.<BizCourse>lambdaQuery()
				.eq(BizCourse::getDelFlag, "0")
				.eq(collegeId != null, BizCourse::getCollegeId, collegeId)
				.orderByAsc(BizCourse::getCourseCode));
	}

	@Override
	public IPage<BizCourse> pageCourse(Page page, Long collegeId, String courseCode, String courseName) {
		LambdaQueryWrapper<BizCourse> wrapper = Wrappers.<BizCourse>lambdaQuery()
				.eq(BizCourse::getDelFlag, "0")
				.eq(collegeId != null, BizCourse::getCollegeId, collegeId)
				.like(courseCode != null, BizCourse::getCourseCode, courseCode)
				.like(courseName != null, BizCourse::getCourseName, courseName)
				.orderByAsc(BizCourse::getCourseCode);
		return this.page(page, wrapper);
	}

	@Override
	public BizCourse getCourseDetail(String courseCode) {
		BizCourse course = this.getOne(Wrappers.<BizCourse>lambdaQuery()
				.eq(BizCourse::getCourseCode, courseCode)
				.eq(BizCourse::getDelFlag, "0"));
		if (course == null) {
			throw new CheckedException("课程不存在");
		}
		return course;
	}

	@Override
	public Boolean addCourse(BizCourse course) {
		// 校验课程号唯一
		long count = this.count(Wrappers.<BizCourse>lambdaQuery()
				.eq(BizCourse::getCourseCode, course.getCourseCode())
				.eq(BizCourse::getDelFlag, "0"));
		if (count > 0) {
			throw new CheckedException("课程号 [" + course.getCourseCode() + "] 已存在");
		}
		course.setCreateTime(new Date());
		course.setUpdateTime(new Date());
		course.setDelFlag("0");
		return this.save(course);
	}

	@Override
	public Boolean updateCourse(BizCourse course) {
		BizCourse existing = this.getOne(Wrappers.<BizCourse>lambdaQuery()
				.eq(BizCourse::getCourseCode, course.getCourseCode())
				.eq(BizCourse::getDelFlag, "0"));
		if (existing == null) {
			throw new CheckedException("课程不存在");
		}
		course.setUpdateTime(new Date());
		return this.updateById(course);
	}

	@Override
	public Boolean removeCourse(String courseCode) {
		BizCourse existing = this.getOne(Wrappers.<BizCourse>lambdaQuery()
				.eq(BizCourse::getCourseCode, courseCode)
				.eq(BizCourse::getDelFlag, "0"));
		if (existing == null) {
			throw new CheckedException("课程不存在");
		}
		// 检查是否有进行中的任课
		long teachingCount = teachingMapper.selectCount(Wrappers.<BizTeaching>lambdaQuery()
				.eq(BizTeaching::getCourseCode, courseCode)
				.eq(BizTeaching::getDelFlag, "0")
				.in(BizTeaching::getStatus, "0", "1"));
		if (teachingCount > 0) {
			throw new CheckedException("该课程还有进行中或未开始的任课记录，无法删除");
		}
		existing.setDelFlag("1");
		existing.setUpdateTime(new Date());
		return this.updateById(existing);
	}
}
