package com.pig4cloud.pig.homework.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.exception.CheckedException;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.pig4cloud.pig.homework.entity.BizHomeworkAttachment;
import com.pig4cloud.pig.homework.entity.BizHomework;
import com.pig4cloud.pig.homework.service.BizHomeworkService;
import com.pig4cloud.pig.homework.mapper.BizHomeworkAttachmentMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/homework")
@Tag(name = "作业管理", description = "homework")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class BizHomeworkController {

    private final BizHomeworkService            homeworkService;
    private final BizHomeworkAttachmentMapper   attachmentMapper;

    @Operation(summary = "分页查询某任课的作业列表")
    @GetMapping("/page")
	@HasPermission("biz_homework_list")
	public R getPage(@ParameterObject Page page, @RequestParam Long teachingId) {
        return R.ok(homeworkService.page(page, Wrappers.<BizHomework>lambdaQuery()
                .eq(BizHomework::getTeachingId, teachingId)
                .eq(BizHomework::getDelFlag, "0")
                .orderByDesc(BizHomework::getDeadline)));
    }

    @Operation(summary = "获取作业详情（学生视角含提交状态，教师视角含统计）")
    @GetMapping("/{homeworkId}")
	@HasPermission("biz_homework_list")
	public R getDetail(@PathVariable Long homeworkId,
                       @RequestParam(required = false) Boolean studentView) {
        Long studentId = Boolean.TRUE.equals(studentView) ? SecurityUtils.getUser().getId() : null;
        return R.ok(homeworkService.getHomeworkDetail(homeworkId, studentId));
    }

    @Operation(summary = "发布作业（含附件，自动推送消息）")
    @PostMapping
    @HasPermission("biz_homework_add")
    @Transactional(rollbackFor = Exception.class)
    public R save(@RequestBody BizHomework homework,
                  @RequestParam(required = false) List<String> fileUrls,
                  @RequestParam(required = false) List<String> fileNames,
                  @RequestParam(required = false) List<Long>   fileSizes,
                  @RequestParam(required = false) List<String> fileTypes) {
        // 业务校验
        if (homework.getTeachingId() == null) {
            throw new CheckedException("任课ID不能为空");
        }
        if (homework.getTitle() == null || homework.getTitle().trim().isEmpty()) {
            throw new CheckedException("作业标题不能为空");
        }
        if (homework.getDeadline() == null) {
            throw new CheckedException("截止时间不能为空");
        }
        homeworkService.save(homework);

        // 保存附件
        if (fileUrls != null) {
            for (int i = 0; i < fileUrls.size(); i++) {
                BizHomeworkAttachment att = new BizHomeworkAttachment();
                att.setHomeworkId(homework.getHomeworkId());
                att.setFileUrl(fileUrls.get(i));
                att.setFileName(fileNames != null && i < fileNames.size() ? fileNames.get(i) : "");
                att.setFileSize(fileSizes != null && i < fileSizes.size() ? fileSizes.get(i) : null);
                att.setFileType(fileTypes != null && i < fileTypes.size() ? fileTypes.get(i) : "other");
                attachmentMapper.insert(att);
            }
        }

        // 推送消息给所有学生
        homeworkService.pushHomeworkMessages(homework.getHomeworkId());

        return R.ok(homework.getHomeworkId());
    }

    @Operation(summary = "修改作业")
    @PutMapping
    @HasPermission("biz_homework_edit")
    public R updateById(@RequestBody BizHomework homework) {
        return R.ok(homeworkService.updateById(homework));
    }

    @Operation(summary = "删除作业")
    @DeleteMapping
    @HasPermission("biz_homework_del")
    @Transactional(rollbackFor = Exception.class)
    public R removeByIds(@RequestBody Long[] ids) {
        // 逻辑删除关联附件
        for (Long id : ids) {
            attachmentMapper.update(null, Wrappers.<BizHomeworkAttachment>lambdaUpdate()
                    .eq(BizHomeworkAttachment::getHomeworkId, id)
                    .set(BizHomeworkAttachment::getDelFlag, "1"));
        }
        return R.ok(homeworkService.removeBatchByIds(Arrays.asList(ids)));
    }
}
