package com.pig4cloud.pig.homework.controller;

import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.pig4cloud.pig.homework.entity.BizCourseware;
import com.pig4cloud.pig.homework.service.BizCoursewareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课件管理
 *
 * @author EyjaSakura
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/courseware")
@Tag(name = "课件管理", description = "courseware")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class BizCoursewareController {

	private final BizCoursewareService coursewareService;

	/**
	 * 上传课件（教师/助教用）
	 */
	@Operation(summary = "上传课件（教师/助教用）")
	@HasPermission("biz_teaching_edit")
	@PostMapping
	public R save(@RequestBody BizCourseware courseware) {
		return R.ok(coursewareService.addCourseware(courseware));
	}

	/**
	 * 获取某任课的课件列表（学生/教师均可用）
	 */
	@Operation(summary = "获取某任课的课件列表")
	@GetMapping("/list/{teachingId}")
	public R list(@PathVariable Long teachingId,
				  @RequestParam(required = false) String folderName) {
		List<BizCourseware> list = coursewareService.listByTeaching(teachingId, folderName);
		return R.ok(list);
	}

	/**
	 * 删除课件（仅上传者或教师可删）
	 */
	@Operation(summary = "删除课件")
	@HasPermission("biz_courseware_del")
	@DeleteMapping("/{coursewareId}")
	public R delete(@PathVariable Long coursewareId) {
		return R.ok(coursewareService.removeCourseware(coursewareId));
	}
}
