package com.pig4cloud.pig.homework.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.common.core.exception.CheckedException;
import com.pig4cloud.pig.homework.entity.*;
import com.pig4cloud.pig.homework.mapper.BizCourseMapper;
import com.pig4cloud.pig.homework.mapper.BizTeachingMapper;
import com.pig4cloud.pig.homework.service.BizEnrollmentService;
import com.pig4cloud.pig.homework.service.BizTeachingService;
import com.pig4cloud.pig.homework.vo.TeachingDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 任课 Service 实现类
 *
 * @author EyjaSakura
 */
@Service
@RequiredArgsConstructor
public class BizTeachingServiceImpl extends ServiceImpl<BizTeachingMapper, BizTeaching>
		implements BizTeachingService {

	private final BizCourseMapper courseMapper;
	private final com.pig4cloud.pig.homework.mapper.BizCollegeMapper collegeMapper;
	private final com.pig4cloud.pig.homework.mapper.BizTeachingAssistantMapper taMapper;
	private final BizEnrollmentService enrollmentService;

	/**
	 * 分页查询任课列表（联查课程名、学院名）
	 */
	@Override
	public IPage<TeachingDetailVO> pageTeaching(Page page, Long collegeId, String courseCode,
												 Integer teachYear, Long teacherId, Integer status) {
		LambdaQueryWrapper<BizTeaching> wrapper = Wrappers.<BizTeaching>lambdaQuery()
				.eq(BizTeaching::getDelFlag, "0")
				.eq(collegeId != null, BizTeaching::getCollegeId, collegeId)
				.like(courseCode != null, BizTeaching::getCourseCode, courseCode)
				.eq(teachYear != null, BizTeaching::getTeachYear, teachYear)
				.eq(teacherId != null, BizTeaching::getTeacherId, teacherId)
				.eq(status != null, BizTeaching::getStatus, status)
				.orderByDesc(BizTeaching::getCreateTime);

		IPage<BizTeaching> teachingPage = this.page(page, wrapper);
		return teachingPage.convert(this::fillTeachingVO);
	}

	/**
	 * 教师查看自己的任课列表
	 */
	@Override
	public List<TeachingDetailVO> listMyTeachings(Long teacherId, Integer teachYear, Integer status) {
		List<BizTeaching> teachings = this.list(Wrappers.<BizTeaching>lambdaQuery()
				.eq(BizTeaching::getDelFlag, "0")
				.eq(BizTeaching::getTeacherId, teacherId)
				.eq(teachYear != null, BizTeaching::getTeachYear, teachYear)
				.eq(status != null, BizTeaching::getStatus, status)
				.orderByDesc(BizTeaching::getCreateTime));
		return teachings.stream().map(this::fillTeachingVO).collect(Collectors.toList());
	}

	/**
	 * 获取任课详情（联查课程名、教师名、助教）
	 */
	@Override
	public TeachingDetailVO getTeachingDetail(Long teachingId) {
		BizTeaching teaching = this.getOne(Wrappers.<BizTeaching>lambdaQuery()
				.eq(BizTeaching::getTeachingId, teachingId)
				.eq(BizTeaching::getDelFlag, "0"));
		if (teaching == null) {
			throw new CheckedException("任课记录不存在");
		}

		TeachingDetailVO vo = fillTeachingVO(teaching);

		// 查询助教
		BizTeachingAssistant ta = taMapper.selectOne(Wrappers.<BizTeachingAssistant>lambdaQuery()
				.eq(BizTeachingAssistant::getTeachingId, teachingId)
				.eq(BizTeachingAssistant::getDelFlag, "0")
				.last("LIMIT 1"));
		if (ta != null) {
			vo.setAssistantId(ta.getAssistantId());
		}

		return vo;
	}

	/**
	 * 学生查看可选课列表（已排除已选和已满）
	 */
	@Override
	public List<TeachingDetailVO> listAvailableForStudent(Long studentId, Integer teachYear) {
		// 1. 查询学生已选的任课ID
		List<BizEnrollment> enrollments = enrollmentService.list(Wrappers.<BizEnrollment>lambdaQuery()
				.eq(BizEnrollment::getStudentId, studentId)
				.eq(BizEnrollment::getStatus, "0")
				.eq(BizEnrollment::getDelFlag, "0"));
		Set<Long> enrolledTeachingIds = enrollments.stream()
				.map(BizEnrollment::getTeachingId)
				.collect(Collectors.toSet());

		// 2. 查询可选的任课（未满、未开始或进行中）
		List<BizTeaching> teachings = this.list(Wrappers.<BizTeaching>lambdaQuery()
				.eq(BizTeaching::getDelFlag, "0")
				.notIn(CollUtil.isNotEmpty(enrolledTeachingIds), BizTeaching::getTeachingId, enrolledTeachingIds)
				.apply("enrolled_count < max_students")
				.in(BizTeaching::getStatus, "0", "1")
				.eq(teachYear != null, BizTeaching::getTeachYear, teachYear)
				.orderByDesc(BizTeaching::getCreateTime));

		return teachings.stream()
				.map(this::fillTeachingVO)
				.collect(Collectors.toList());
	}

	/**
	 * 创建任课
	 */
	@Override
	public Boolean addTeaching(BizTeaching teaching) {
		// 校验课程存在
		BizCourse course = courseMapper.selectById(teaching.getCourseCode());
		if (course == null || "1".equals(course.getDelFlag())) {
			throw new CheckedException("课程号 [" + teaching.getCourseCode() + "] 不存在");
		}
		// 自动填充学院ID
		if (teaching.getCollegeId() == null) {
			teaching.setCollegeId(course.getCollegeId());
		}
		teaching.setEnrolledCount(0);
		teaching.setStatus("0"); // 默认未开始
		teaching.setCreateTime(new Date());
		teaching.setUpdateTime(new Date());
		teaching.setDelFlag("0");
		return this.save(teaching);
	}

	/**
	 * 修改任课
	 */
	@Override
	public Boolean updateTeaching(BizTeaching teaching) {
		BizTeaching existing = this.getById(teaching.getTeachingId());
		if (existing == null || "1".equals(existing.getDelFlag())) {
			throw new CheckedException("任课记录不存在");
		}
		// 如果修改了人数上限，不能小于已选人数
		if (teaching.getMaxStudents() != null && existing.getEnrolledCount() != null
				&& teaching.getMaxStudents() < existing.getEnrolledCount()) {
			throw new CheckedException("人数上限不能小于当前已选人数 [" + existing.getEnrolledCount() + "]");
		}
		teaching.setUpdateTime(new Date());
		return this.updateById(teaching);
	}

	/**
	 * 删除任课（逻辑删除）
	 */
	@Override
	public Boolean removeTeaching(Long teachingId) {
		BizTeaching existing = this.getById(teachingId);
		if (existing == null || "1".equals(existing.getDelFlag())) {
			throw new CheckedException("任课记录不存在");
		}
		// 检查是否有已选课学生
		long enrollmentCount = enrollmentService.count(Wrappers.<BizEnrollment>lambdaQuery()
				.eq(BizEnrollment::getTeachingId, teachingId)
				.eq(BizEnrollment::getStatus, "0")
				.eq(BizEnrollment::getDelFlag, "0"));
		if (enrollmentCount > 0) {
			throw new CheckedException("该任课下已有学生选课，无法删除。请先让学生退课。");
		}
		existing.setDelFlag("1");
		existing.setUpdateTime(new Date());
		return this.updateById(existing);
	}

	// ===================== 私有辅助方法 =====================

	/**
	 * 将 BizTeaching 实体填充为 TeachingDetailVO（课程名、学院名等）
	 */
	private TeachingDetailVO fillTeachingVO(BizTeaching t) {
		TeachingDetailVO vo = new TeachingDetailVO();
		vo.setTeachingId(t.getTeachingId());
		vo.setCourseCode(t.getCourseCode());
		vo.setTeachYear(t.getTeachYear());
		vo.setClassNo(t.getClassNo());
		vo.setTeacherId(t.getTeacherId());
		vo.setTimePlace(t.getTimePlace());
		vo.setMaxStudents(t.getMaxStudents());
		vo.setEnrolledCount(t.getEnrolledCount());
		vo.setStatus(t.getStatus());

		// 关联课程信息
		BizCourse course = courseMapper.selectById(t.getCourseCode());
		if (course != null) {
			vo.setCourseName(course.getCourseName());
			vo.setCredit(course.getCredit());
		}

		// 关联学院信息
		if (t.getCollegeId() != null) {
			BizCollege college = collegeMapper.selectById(t.getCollegeId());
			if (college != null) {
				vo.setCollegeName(college.getCollegeName());
			}
		}

		return vo;
	}
}
