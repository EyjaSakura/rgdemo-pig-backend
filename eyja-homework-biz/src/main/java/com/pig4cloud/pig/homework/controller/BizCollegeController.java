package com.pig4cloud.pig.homework.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import com.pig4cloud.pig.homework.service.BizCollegeService;
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
 * 学院管理
 *
 * @author EyjaSakura
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/college")
@Tag(name = "学院管理", description = "college")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class BizCollegeController {

	private final BizCollegeService collegeService;

	/**
	 * 获取学院列表（下拉框用，不分页）
	 */
	@Operation(summary = "获取全部学院列表（下拉框）")
	@GetMapping("/list")
	public R<List> list() {
		return R.ok(collegeService.listAll());
	}

	/**
	 * 从 sys_dept 同步学院数据
	 */
	@Operation(summary = "从 sys_dept 同步学院数据到 biz_college")
	@GetMapping("/syncFromDept")
	@HasPermission("biz_college_add")
	public R<Map<String, Object>> syncFromDept() {
		return R.ok(collegeService.syncFromDept());
	}

	/**
	 * 分页查询学院
	 */
	@Operation(summary = "分页查询学院列表")
	@GetMapping("/page")
	@HasPermission("biz_college_list")
	public R page(@ParameterObject Page page,
				  @RequestParam(required = false) String collegeName) {
		return R.ok(collegeService.pageCollege(page, collegeName));
	}

	/**
	 * 新增学院
	 */
	@Operation(summary = "新增学院")
	@PostMapping
	@HasPermission("biz_college_add")
	public R save(@RequestBody com.pig4cloud.pig.homework.entity.BizCollege college) {
		return R.ok(collegeService.addCollege(college));
	}

	/**
	 * 修改学院
	 */
	@Operation(summary = "修改学院信息")
	@PutMapping
	@HasPermission("biz_college_edit")
	public R update(@RequestBody com.pig4cloud.pig.homework.entity.BizCollege college) {
		return R.ok(collegeService.updateCollege(college));
	}

	/**
	 * 删除学院
	 */
	@Operation(summary = "删除学院（逻辑删除）")
	@DeleteMapping("/{collegeId}")
	@HasPermission("biz_college_del")
	public R delete(@PathVariable Long collegeId) {
		return R.ok(collegeService.removeCollege(collegeId));
	}
}
