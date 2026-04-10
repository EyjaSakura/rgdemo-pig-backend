package com.pig4cloud.pig.homework.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.common.core.exception.CheckedException;
import com.pig4cloud.pig.homework.entity.*;
import com.pig4cloud.pig.homework.mapper.BizEnrollmentMapper;
import com.pig4cloud.pig.homework.service.BizEnrollmentService;
import com.pig4cloud.pig.homework.vo.EnrollmentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 选课 Service 实现类
 *
 * @author EyjaSakura
 */
@Service
@RequiredArgsConstructor
public class BizEnrollmentServiceImpl extends ServiceImpl<BizEnrollmentMapper, BizEnrollment>
		implements BizEnrollmentService {

	private final com.pig4cloud.pig.homework.mapper.BizTeachingMapper teachingMapper;
	private final com.pig4cloud.pig.homework.mapper.BizCourseMapper    courseMapper;

	/**
	 * 学生选课
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void enroll(Long teachingId, Long studentId) {
		// 1. 检查任课是否存在且可选
		BizTeaching teaching = teachingMapper.selectById(teachingId);
		if (teaching == null || "1".equals(teaching.getDelFlag())) {
			throw new CheckedException("任课记录不存在");
		}
		if (!"0".equals(teaching.getStatus()) && !"1".equals(teaching.getStatus())) {
			throw new CheckedException("当前任课不在可选课状态");
		}
		if (teaching.getEnrolledCount() >= teaching.getMaxStudents()) {
			throw new CheckedException("选课人数已满");
		}

		// 2. 检查是否已选
		long exists = this.count(Wrappers.<BizEnrollment>lambdaQuery()
				.eq(BizEnrollment::getTeachingId, teachingId)
				.eq(BizEnrollment::getStudentId, studentId)
				.eq(BizEnrollment::getStatus, "0")
				.eq(BizEnrollment::getDelFlag, "0"));
		if (exists > 0) {
			throw new CheckedException("已选过此课程");
		}

		// 3. 检查是否退课后重选
		BizEnrollment dropped = this.getOne(Wrappers.<BizEnrollment>lambdaQuery()
				.eq(BizEnrollment::getTeachingId, teachingId)
				.eq(BizEnrollment::getStudentId, studentId)
				.eq(BizEnrollment::getStatus, "1"));
		if (dropped != null) {
			// 恢复退课记录
			dropped.setStatus("0");
			dropped.setEnrollTime(new Date());
			this.updateById(dropped);
		} else {
			// 新增选课记录
			BizEnrollment enrollment = BizEnrollment.builder()
					.teachingId(teachingId)
					.studentId(studentId)
					.enrollTime(new Date())
					.status("0")
					.delFlag("0")
					.build();
			this.save(enrollment);
		}

		// 4. 更新已选人数
		teaching.setEnrolledCount(teaching.getEnrolledCount() + 1);
		teachingMapper.updateById(teaching);
	}

	/**
	 * 学生退课
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void drop(Long teachingId, Long studentId) {
		BizEnrollment enrollment = this.getOne(Wrappers.<BizEnrollment>lambdaQuery()
				.eq(BizEnrollment::getTeachingId, teachingId)
				.eq(BizEnrollment::getStudentId, studentId)
				.eq(BizEnrollment::getStatus, "0")
				.eq(BizEnrollment::getDelFlag, "0"));
		if (enrollment == null) {
			throw new CheckedException("未找到有效的选课记录");
		}

		enrollment.setStatus("1");
		this.updateById(enrollment);

		// 更新已选人数（不少于0）
		BizTeaching teaching = teachingMapper.selectById(teachingId);
		if (teaching != null && teaching.getEnrolledCount() > 0) {
			teaching.setEnrolledCount(teaching.getEnrolledCount() - 1);
			teachingMapper.updateById(teaching);
		}
	}

	/**
	 * 查询某任课下的学生名单（分页）
	 */
	@Override
	public IPage<EnrollmentVO> pageStudentsByTeaching(Page page, Long teachingId) {
		// 分页查询选课记录
		LambdaQueryWrapper<BizEnrollment> wrapper = Wrappers.<BizEnrollment>lambdaQuery()
				.eq(BizEnrollment::getTeachingId, teachingId)
				.eq(BizEnrollment::getDelFlag, "0")
				.orderByDesc(BizEnrollment::getEnrollTime);

		IPage<BizEnrollment> enrollmentPage = this.page(page, wrapper);

		// 批量填充 VO
		return enrollmentPage.convert(e -> {
			EnrollmentVO vo = new EnrollmentVO();
			vo.setId(e.getEnrollmentId());
			vo.setTeachingId(e.getTeachingId());
			vo.setStudentId(e.getStudentId());
			vo.setEnrolledAt(e.getEnrollTime() != null ? new java.sql.Timestamp(e.getEnrollTime().getTime()).toLocalDateTime() : null);
			vo.setIsActive("0".equals(e.getStatus()) ? 1 : 0);

			// 填充课程信息
			BizTeaching teaching = teachingMapper.selectById(e.getTeachingId());
			if (teaching != null) {
				vo.setCourseCode(teaching.getCourseCode());
				vo.setTeachYear(teaching.getTeachYear());
				vo.setClassNo(teaching.getClassNo());
				BizCourse course = courseMapper.selectById(teaching.getCourseCode());
				if (course != null) {
					vo.setCourseName(course.getCourseName());
					vo.setCredit(course.getCredit());
				}
			}

			return vo;
		});
	}

	/**
	 * 查询某学生的选课列表
	 */
	@Override
	public List<EnrollmentVO> listByStudent(Long studentId, Integer isActive) {
		LambdaQueryWrapper<BizEnrollment> wrapper = Wrappers.<BizEnrollment>lambdaQuery()
				.eq(BizEnrollment::getStudentId, studentId)
				.eq(BizEnrollment::getDelFlag, "0")
				.eq(isActive != null, BizEnrollment::getStatus, isActive == 1 ? "0" : "1")
				.orderByDesc(BizEnrollment::getEnrollTime);

		List<BizEnrollment> enrollments = this.list(wrapper);
		if (CollUtil.isEmpty(enrollments)) {
			return Collections.emptyList();
		}

		// 批量获取关联的任课和课程信息
		Set<Long> teachingIds = enrollments.stream()
				.map(BizEnrollment::getTeachingId)
				.collect(Collectors.toSet());
		Map<Long, BizTeaching> teachingMap = teachingMapper.selectBatchIds(teachingIds).stream()
				.collect(Collectors.toMap(BizTeaching::getTeachingId, t -> t));

		Set<String> courseCodes = teachingMap.values().stream()
				.map(BizTeaching::getCourseCode)
				.collect(Collectors.toSet());
		Map<String, BizCourse> courseMap = courseMapper.selectBatchIds(courseCodes).stream()
				.collect(Collectors.toMap(BizCourse::getCourseCode, c -> c));

		// 组装 VO
		return enrollments.stream().map(e -> {
			EnrollmentVO vo = new EnrollmentVO();
			vo.setId(e.getEnrollmentId());
			vo.setTeachingId(e.getTeachingId());
			vo.setStudentId(e.getStudentId());
			vo.setEnrolledAt(e.getEnrollTime() != null ? new java.sql.Timestamp(e.getEnrollTime().getTime()).toLocalDateTime() : null);
			vo.setIsActive("0".equals(e.getStatus()) ? 1 : 0);

			BizTeaching teaching = teachingMap.get(e.getTeachingId());
			if (teaching != null) {
				vo.setCourseCode(teaching.getCourseCode());
				vo.setTeachYear(teaching.getTeachYear());
				vo.setClassNo(teaching.getClassNo());

				BizCourse course = courseMap.get(teaching.getCourseCode());
				if (course != null) {
					vo.setCourseName(course.getCourseName());
					vo.setCredit(course.getCredit());
				}
			}

			return vo;
		}).collect(Collectors.toList());
	}
}
