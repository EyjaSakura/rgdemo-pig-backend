package com.pig4cloud.pig.homework.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import com.pig4cloud.pig.homework.entity.BizCourse;
import com.pig4cloud.pig.homework.service.BizCourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 课程管理
 *
 * @author EyjaSakura
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/course")
@Tag(name = "课程管理", description = "course")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class BizCourseController {

	private final BizCourseService courseService;

	/**
	 * 获取学院-课程树形结构（任课管理左侧树用）
	 */
	@Operation(summary = "获取学院-课程树形结构")
	@GetMapping("/tree")
	public R<List<Map<String, Object>>> courseTree(@RequestParam(required = false) String courseName) {
		return R.ok(courseService.courseTree(courseName));
	}

	/**
	 * 获取课程列表（下拉框用，可按学院过滤）
	 */
	@Operation(summary = "获取课程列表（下拉框，可按学院过滤）")
	@GetMapping("/list")
	public R<List> list(@RequestParam(required = false) Long collegeId) {
		return R.ok(courseService.listCourses(collegeId));
	}

	/**
	 * 分页查询课程
	 */
	@Operation(summary = "分页查询课程列表（支持学院/课程号/课程名筛选）")
	@GetMapping("/page")
	@HasPermission("biz_course_list")
	public R page(@ParameterObject Page page,
				  @RequestParam(required = false) Long collegeId,
				  @RequestParam(required = false) String courseCode,
				  @RequestParam(required = false) String courseName) {
		IPage<BizCourse> result = courseService.pageCourse(page, collegeId, courseCode, courseName);
		return R.ok(result);
	}

	/**
	 * 获取课程详情
	 */
	@Operation(summary = "获取课程详情")
	@GetMapping("/{courseCode}")
	public R detail(@PathVariable String courseCode) {
		return R.ok(courseService.getCourseDetail(courseCode));
	}

	/**
	 * 新增课程
	 */
	@Operation(summary = "新增课程")
	@PostMapping
	@HasPermission("biz_course_add")
	public R save(@RequestBody BizCourse course) {
		return R.ok(courseService.addCourse(course));
	}

	/**
	 * 修改课程
	 */
	@Operation(summary = "修改课程信息")
	@PutMapping
	@HasPermission("biz_course_edit")
	public R update(@RequestBody BizCourse course) {
		return R.ok(courseService.updateCourse(course));
	}

	/**
	 * 删除课程
	 */
	@Operation(summary = "删除课程（逻辑删除）")
	@DeleteMapping("/{courseCode}")
	@HasPermission("biz_course_del")
	public R delete(@PathVariable String courseCode) {
		return R.ok(courseService.removeCourse(courseCode));
	}
}
