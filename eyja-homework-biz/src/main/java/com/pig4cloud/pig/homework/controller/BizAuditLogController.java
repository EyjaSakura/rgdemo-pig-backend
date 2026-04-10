package com.pig4cloud.pig.homework.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import com.pig4cloud.pig.homework.entity.BizAuditLog;
import com.pig4cloud.pig.homework.service.BizAuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 操作审计日志表 前端控制器
 *
 * @author EyjaSakura
 * @since 2026-04-08 20:28
 */
@Tag(name = "审计日志管理", description = "audit-log")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auditlog")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class BizAuditLogController {

    private final BizAuditLogService auditLogService;

    @Operation(summary = "分页查询审计日志")
    @GetMapping("/page")
    @HasPermission("biz_audit_log_view")
    public R getPage(@ParameterObject Page page,
                     @RequestParam(required = false) String username,
                     @RequestParam(required = false) String module,
                     @RequestParam(required = false) String action,
                     @RequestParam(required = false) Date startTime,
                     @RequestParam(required = false) Date endTime) {
        return R.ok(auditLogService.page(page, Wrappers.<BizAuditLog>lambdaQuery()
                .like(username != null && !username.isEmpty(), BizAuditLog::getUsername, username)
                .like(module != null && !module.isEmpty(), BizAuditLog::getModule, module)
                .like(action != null && !action.isEmpty(), BizAuditLog::getAction, action)
                .ge(startTime != null, BizAuditLog::getCreateTime, startTime)
                .le(endTime != null, BizAuditLog::getCreateTime, endTime)
                .orderByDesc(BizAuditLog::getCreateTime)));
    }

    @Operation(summary = "获取审计日志详情")
    @GetMapping("/{logId}")
    @HasPermission("biz_audit_log_view")
    public R getDetail(@Parameter(description = "日志ID") @PathVariable Long logId) {
        return R.ok(auditLogService.getById(logId));
    }
}
