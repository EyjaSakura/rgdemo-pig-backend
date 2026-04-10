package com.pig4cloud.pig.homework.vo;

import com.pig4cloud.pig.homework.entity.BizHomeworkAttachment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 作业详情 VO（含附件、当前学生的提交状态）
 */
@Data
@Schema(description = "作业详情")
public class HomeworkDetailVO {

    @Schema(description = "作业ID")
    private Long homeworkId;

    @Schema(description = "任课ID")
    private Long teachingId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "作业标题")
    private String title;

    @Schema(description = "作业描述")
    private String description;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "截止时间")
    private LocalDateTime deadline;

    @Schema(description = "教师附件列表")
    private List<BizHomeworkAttachment> attachments;

    // ====== 以下字段仅学生视角时有值 ======

    @Schema(description = "当前学生的提交ID（未提交为null）")
    private Long submissionId;

    @Schema(description = "提交状态(0未交 1已交待批 2已批阅)")
    private String submissionStatus;

    @Schema(description = "得分（已批阅时有值）")
    private BigDecimal score;

    @Schema(description = "批阅评语")
    private String teacherComment;

    @Schema(description = "提交时间")
    private LocalDateTime submitTime;

    // ====== 以下字段仅教师/管理员视角时有值 ======

    @Schema(description = "总提交人数")
    private Integer submittedCount;

    @Schema(description = "待批改数")
    private Integer pendingGradeCount;
}
