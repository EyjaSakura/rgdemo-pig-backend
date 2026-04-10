package com.pig4cloud.pig.homework.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.pig4cloud.pig.homework.entity.BizMessage;
import com.pig4cloud.pig.homework.service.BizMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
@Tag(name = "消息中心", description = "message")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class BizMessageController {

	private final BizMessageService messageService;

	/**
	 * 分页查询当前用户的消息列表
	 */
	@Operation(summary = "分页查询当前用户的消息列表")
	@HasPermission("biz_message_list")
	@GetMapping("/page")
	public R page(@ParameterObject Page page,
				  @RequestParam(required = false) String type,
				  @RequestParam(required = false) Integer isRead) {
		Long userId = SecurityUtils.getUser().getId();
		return R.ok(messageService.page(page, Wrappers.<BizMessage>lambdaQuery()
				.eq(BizMessage::getReceiverId, userId)
				.eq(BizMessage::getDelFlag, "0")
				.eq(type != null, BizMessage::getType, type)
				.eq(isRead != null, BizMessage::getIsRead, isRead)
				.orderByDesc(BizMessage::getCreateTime)));
	}

	/**
	 * 获取消息详情
	 */
	@Operation(summary = "获取消息详情")
	@GetMapping("/{messageId}")
	public R detail(@PathVariable Long messageId) {
		Long userId = SecurityUtils.getUser().getId();
		BizMessage msg = messageService.getOne(Wrappers.<BizMessage>lambdaQuery()
				.eq(BizMessage::getMessageId, messageId)
				.eq(BizMessage::getReceiverId, userId)
				.eq(BizMessage::getDelFlag, "0"));
		if (msg == null) {
			return R.failed("消息不存在或无权访问");
		}
		// 自动标记为已读
		if (msg.getIsRead() == 0) {
			msg.setIsRead(1);
			messageService.updateById(msg);
		}
		return R.ok(msg);
	}

	/**
	 * 统计当前用户未读消息数
	 */
	@Operation(summary = "统计当前用户未读消息数")
	@GetMapping("/unread-count")
	public R unreadCount() {
		Long userId = SecurityUtils.getUser().getId();
		return R.ok(messageService.countUnread(userId));
	}

	/**
	 * 标记单条消息为已读
	 */
	@Operation(summary = "标记单条消息为已读")
	@PutMapping("/{messageId}/read")
	public R markRead(@PathVariable Long messageId) {
		Long userId = SecurityUtils.getUser().getId();
		boolean updated = messageService.update(Wrappers.<BizMessage>lambdaUpdate()
				.eq(BizMessage::getMessageId, messageId)
				.eq(BizMessage::getReceiverId, userId)
				.eq(BizMessage::getIsRead, 0)
				.set(BizMessage::getIsRead, 1));
		return updated ? R.ok() : R.failed("消息不存在或已读");
	}

	/**
	 * 标记当前用户所有消息为已读
	 */
	@Operation(summary = "标记当前用户所有消息为已读")
	@PutMapping("/read-all")
	public R markAllRead() {
		Long userId = SecurityUtils.getUser().getId();
		messageService.markAllRead(userId);
		return R.ok();
	}

	/**
	 * 删除消息（逻辑删除）
	 */
	@Operation(summary = "删除消息（逻辑删除）")
	@DeleteMapping("/{messageId}")
	public R remove(@PathVariable Long messageId) {
		Long userId = SecurityUtils.getUser().getId();
		boolean deleted = messageService.update(Wrappers.<BizMessage>lambdaUpdate()
				.eq(BizMessage::getMessageId, messageId)
				.eq(BizMessage::getReceiverId, userId)
				.set(BizMessage::getDelFlag, "1"));
		return deleted ? R.ok() : R.failed("消息不存在或无权删除");
	}
}
