package com.pig4cloud.pig.homework.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.pig4cloud.pig.homework.entity.BizTeaching;
import com.pig4cloud.pig.homework.service.BizTeachingService;
import com.pig4cloud.pig.homework.vo.TeachingDetailVO;
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
@RequestMapping("/teaching")
@Tag(name = "任课管理", description = "teaching")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class BizTeachingController {

	private final BizTeachingService teachingService;

	/**
	 * 分页查询任课列表（管理员/教师用）
	 */
	@Operation(summary = "分页查询任课列表（支持学院/课程号/年份/教师/状态筛选）")
	@HasPermission("biz_teaching_list")
	@GetMapping("/page")
	public R page(@ParameterObject Page page,
				  @RequestParam(required = false) Long collegeId,
				  @RequestParam(required = false) String courseCode,
				  @RequestParam(required = false) Integer teachYear,
				  @RequestParam(required = false) Long teacherId,
				  @RequestParam(required = false) Integer status) {
		IPage<TeachingDetailVO> result = teachingService.pageTeaching(page, collegeId, courseCode, teachYear, teacherId, status);
		return R.ok(result);
	}

	/**
	 * 教师查看自己的任课列表
	 */
	@Operation(summary = "教师查看自己的任课列表")
	@HasPermission("biz_teaching_list")
	@GetMapping("/mine")
	public R mine(@RequestParam(required = false) Integer teachYear,
				  @RequestParam(required = false) Integer status) {
		Long teacherId = SecurityUtils.getUser().getId();
		List<TeachingDetailVO> list = teachingService.listMyTeachings(teacherId, teachYear, status);
		return R.ok(list);
	}

	/**
	 * 获取任课详情
	 */
	@Operation(summary = "获取任课详情（含课程名、教师名、助教信息）")
	@HasPermission("biz_teaching_list")
	@GetMapping("/{teachingId}")
	public R detail(@PathVariable Long teachingId) {
		return R.ok(teachingService.getTeachingDetail(teachingId));
	}

	/**
	 * 创建任课
	 */
	@Operation(summary = "创建任课（管理员/教师创建开课记录）")
	@HasPermission("biz_teaching_add")
	@PostMapping
	public R save(@RequestBody BizTeaching teaching) {
		return R.ok(teachingService.addTeaching(teaching));
	}

	/**
	 * 修改任课
	 */
	@Operation(summary = "修改任课信息")
	@HasPermission("biz_teaching_edit")
	@PutMapping
	public R update(@RequestBody BizTeaching teaching) {
		return R.ok(teachingService.updateTeaching(teaching));
	}

	/**
	 * 删除任课（逻辑删除）
	 */
	@Operation(summary = "删除任课（逻辑删除）")
	@HasPermission("biz_teaching_del")
	@DeleteMapping("/{teachingId}")
	public R delete(@PathVariable Long teachingId) {
		return R.ok(teachingService.removeTeaching(teachingId));
	}

	/**
	 * 学生查看可选课列表
	 */
	@Operation(summary = "学生查看可选课列表（已排除已选和已满）")
	@GetMapping("/available")
	public R listAvailable(@RequestParam(required = false) Integer teachYear) {
		Long studentId = SecurityUtils.getUser().getId();
		List<TeachingDetailVO> list = teachingService.listAvailableForStudent(studentId, teachYear);
		return R.ok(list);
	}
}
