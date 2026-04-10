package com.pig4cloud.pig.homework.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.pig4cloud.pig.homework.service.BizEnrollmentService;
import com.pig4cloud.pig.homework.vo.EnrollmentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/enrollment")
@Tag(name = "选课管理", description = "enrollment")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class BizEnrollmentController {

	private final BizEnrollmentService enrollmentService;

	/**
	 * 学生选课
	 */
	@Operation(summary = "学生选课")
	@PostMapping("/{teachingId}")
	public R enroll(@PathVariable Long teachingId) {
		Long studentId = SecurityUtils.getUser().getId();
		enrollmentService.enroll(teachingId, studentId);
		return R.ok();
	}

	/**
	 * 学生退课
	 */
	@Operation(summary = "学生退课")
	@DeleteMapping("/{teachingId}")
	public R drop(@PathVariable Long teachingId) {
		Long studentId = SecurityUtils.getUser().getId();
		enrollmentService.drop(teachingId, studentId);
		return R.ok();
	}

	/**
	 * 教师/助教查看某任课下的学生名单（分页）
	 */
	@Operation(summary = "查看某任课下的学生名单（分页）")
	@HasPermission("biz_teaching_enroll")
	@GetMapping("/students/{teachingId}")
	public R pageStudents(@ParameterObject Page page, @PathVariable Long teachingId) {
		IPage<EnrollmentVO> result = enrollmentService.pageStudentsByTeaching(page, teachingId);
		return R.ok(result);
	}

	/**
	 * 学生查看自己的选课列表
	 */
	@Operation(summary = "学生查看自己的选课列表")
	@GetMapping("/my")
	public R myEnrollments(@RequestParam(required = false) Integer isActive) {
		Long studentId = SecurityUtils.getUser().getId();
		List<EnrollmentVO> list = enrollmentService.listByStudent(studentId, isActive);
		return R.ok(list);
	}
}
