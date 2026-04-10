package com.pig4cloud.pig.homework.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.pig4cloud.pig.homework.entity.BizHomeworkSubmission;
import com.pig4cloud.pig.homework.service.BizHomeworkSubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/submission")
@Tag(name = "作业提交与批阅", description = "submission")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class BizHomeworkSubmissionController {

    private final BizHomeworkSubmissionService submissionService;

    @Operation(summary = "学生提交作业")
    @PostMapping("/submit")
    @HasPermission("biz_homework_submit")
    public R submit(@RequestBody SubmitRequest req) {
        BizHomeworkSubmission sub = new BizHomeworkSubmission();
        sub.setHomeworkId(req.getHomeworkId());
        sub.setStudentId(SecurityUtils.getUser().getId());
        sub.setSubmitContent(req.getSubmitContent());
        submissionService.submitHomework(sub, req.getFileUrls(), req.getFileNames(),
                req.getFileSizes(), req.getFileTypes());
        return R.ok();
    }

    @Operation(summary = "教师/助教批改作业")
    @PostMapping("/grade")
    @HasPermission("biz_homework_grade")
    public R grade(@RequestBody GradeRequest req) {
        Long gradedById = SecurityUtils.getUser().getId();
        submissionService.gradeSubmission(req.getSubmissionId(), req.getScore(),
                req.getComment(), gradedById);
        return R.ok();
    }

    @Operation(summary = "教师/助教分页查看某作业的所有提交")
    @GetMapping("/list/{homeworkId}")
	@HasPermission("biz_submission_list")
	public R pageByHomework(@ParameterObject Page page,
                            @PathVariable Long homeworkId,
                            @RequestParam(required = false) String status) {
        return R.ok(submissionService.pageSubmissionsByHomework(page, homeworkId, status));
    }

    @Operation(summary = "学生查看自己在某门课下的作业及提交状态")
    @GetMapping("/my/{teachingId}")
    @HasPermission("biz_homework_submit")
    public R mySubmissions(@ParameterObject Page page, @PathVariable Long teachingId) {
        Long studentId = SecurityUtils.getUser().getId();
        return R.ok(submissionService.pageStudentSubmissions(page, teachingId, studentId));
    }

    // ===================== 请求体 DTO =====================

    @Data
    public static class SubmitRequest {
        private Long         homeworkId;
        private String       submitContent;
        private List<String> fileUrls;
        private List<String> fileNames;
        private List<Long>   fileSizes;
        private List<String> fileTypes;
    }

    @Data
    public static class GradeRequest {
        private Long       submissionId;
        private BigDecimal score;
        private String     comment;
    }
}
