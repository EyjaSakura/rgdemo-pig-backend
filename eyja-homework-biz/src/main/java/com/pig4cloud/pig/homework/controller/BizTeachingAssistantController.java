package com.pig4cloud.pig.homework.controller;

import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import com.pig4cloud.pig.homework.entity.BizTeachingAssistant;
import com.pig4cloud.pig.homework.service.BizTeachingAssistantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 助教管理
 *
 * @author EyjaSakura
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/assistant")
@Tag(name = "助教管理", description = "assistant")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class BizTeachingAssistantController {

	private final BizTeachingAssistantService assistantService;

	/**
	 * 指派助教（教师用）
	 */
	@Operation(summary = "指派助教")
	@HasPermission("biz_teaching_edit")
	@PostMapping
	public R save(@RequestBody BizTeachingAssistant assistant) {
		return R.ok(assistantService.assignAssistant(assistant));
	}

	/**
	 * 查看某任课的助教列表
	 */
	@Operation(summary = "查看某任课的助教列表")
	@GetMapping("/{teachingId}")
	public R list(@PathVariable Long teachingId) {
		List<BizTeachingAssistant> list = assistantService.listByTeaching(teachingId);
		return R.ok(list);
	}

	/**
	 * 移除助教
	 */
	@Operation(summary = "移除助教")
	@HasPermission("biz_ta_del")
	@DeleteMapping("/{id}")
	public R delete(@PathVariable Long id) {
		return R.ok(assistantService.removeAssistant(id));
	}
}
