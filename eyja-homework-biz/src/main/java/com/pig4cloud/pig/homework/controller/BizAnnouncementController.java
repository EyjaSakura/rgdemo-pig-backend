package com.pig4cloud.pig.homework.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.pig4cloud.pig.homework.entity.BizAnnouncement;
import com.pig4cloud.pig.homework.service.BizAnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

/**
 * 公告管理
 *
 * @author EyjaSakura
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/announcement")
@Tag(name = "公告管理", description = "announcement")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class BizAnnouncementController {

	private final BizAnnouncementService announcementService;

	/**
	 * 发布公告（系统/学院/课程三级）
	 */
	@Operation(summary = "发布公告（teachingId+collegeId 均传为系统级；仅传 collegeId 为学院级；均传为课程级）")
	@HasPermission("biz_announcement_add")
	@PostMapping
	public R save(@RequestBody BizAnnouncement announcement) {
		return R.ok(announcementService.addAnnouncement(announcement));
	}

	/**
	 * 分页查询公告列表
	 */
	@Operation(summary = "分页查询公告列表（按层级过滤）")
	@GetMapping("/page")
	public R page(@ParameterObject Page page,
				  @RequestParam(required = false) Long collegeId,
				  @RequestParam(required = false) Long teachingId) {
		IPage<BizAnnouncement> result = announcementService.pageAnnouncement(page, collegeId, teachingId);
		return R.ok(result);
	}

	/**
	 * 获取公告详情
	 */
	@Operation(summary = "获取公告详情")
	@GetMapping("/{announcementId}")
	public R detail(@PathVariable Long announcementId) {
		return R.ok(announcementService.getDetail(announcementId));
	}

	/**
	 * 修改公告
	 */
	@Operation(summary = "修改公告")
	@HasPermission("biz_announcement_edit")
	@PutMapping
	public R update(@RequestBody BizAnnouncement announcement) {
		return R.ok(announcementService.updateAnnouncement(announcement));
	}

	/**
	 * 删除公告
	 */
	@Operation(summary = "删除公告（逻辑删除）")
	@HasPermission("biz_announcement_del")
	@DeleteMapping("/{announcementId}")
	public R delete(@PathVariable Long announcementId) {
		return R.ok(announcementService.removeAnnouncement(announcementId));
	}
}
