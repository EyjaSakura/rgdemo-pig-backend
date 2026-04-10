package com.pig4cloud.pig.homework.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.pig4cloud.pig.homework.entity.BizLearningRecord;
import com.pig4cloud.pig.homework.service.BizLearningRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

/**
 * 学习记录表 前端控制器
 *
 * @author EyjaSakura
 * @since 2026-04-08 20:28
 */
@Tag(name = "学习记录管理", description = "learning-record")
@RestController
@RequiredArgsConstructor
@RequestMapping("/learningrecord")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class BizLearningRecordController {

    private final BizLearningRecordService learningRecordService;

    @Operation(summary = "分页查询学习记录（管理员/教师）")
    @GetMapping("/page")
    @HasPermission("biz_learning_record_view")
    public R getPage(@ParameterObject Page page,
                     @RequestParam(required = false) Long studentId,
                     @RequestParam(required = false) Long teachingId,
                     @RequestParam(required = false) String actionType) {
        return R.ok(learningRecordService.page(page, Wrappers.<BizLearningRecord>lambdaQuery()
                .eq(studentId != null, BizLearningRecord::getStudentId, studentId)
                .eq(teachingId != null, BizLearningRecord::getTeachingId, teachingId)
                .eq(actionType != null && !actionType.isEmpty(), BizLearningRecord::getActionType, actionType)
                .orderByDesc(BizLearningRecord::getCreateTime)));
    }

    @Operation(summary = "上报学习记录（学生视角）")
    @PostMapping("/report")
    @HasPermission("biz_learning_record_report")
    public R report(@RequestBody BizLearningRecord record) {
        record.setStudentId(SecurityUtils.getUser().getId());
        return R.ok(learningRecordService.save(record));
    }

    @Operation(summary = "我的学习记录（学生视角）")
    @GetMapping("/my")
    public R getMyRecords(@ParameterObject Page page,
                          @RequestParam(required = false) Long teachingId,
                          @RequestParam(required = false) String actionType) {
        Long currentUserId = SecurityUtils.getUser().getId();
        return R.ok(learningRecordService.page(page, Wrappers.<BizLearningRecord>lambdaQuery()
                .eq(BizLearningRecord::getStudentId, currentUserId)
                .eq(teachingId != null, BizLearningRecord::getTeachingId, teachingId)
                .eq(actionType != null && !actionType.isEmpty(), BizLearningRecord::getActionType, actionType)
                .orderByDesc(BizLearningRecord::getCreateTime)));
    }
}
