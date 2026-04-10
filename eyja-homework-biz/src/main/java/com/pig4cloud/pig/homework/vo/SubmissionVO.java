package com.pig4cloud.pig.homework.vo;

import com.pig4cloud.pig.homework.entity.BizSubmissionFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 作业提交 VO（含学生信息、提交文件）
 */
@Data
@Schema(description = "作业提交详情")
public class SubmissionVO {

    @Schema(description = "提交记录ID")
    private Long submissionId;

    @Schema(description = "作业ID")
    private Long homeworkId;

    @Schema(description = "作业标题")
    private String homeworkTitle;

    @Schema(description = "截止时间")
    private LocalDateTime deadline;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "学生账号（学号）")
    private String studentUsername;

    @Schema(description = "提交正文")
    private String submitContent;

    @Schema(description = "状态(0未交 1已交待批 2已批阅 3逾期)")
    private String status;

    @Schema(description = "评分")
    private BigDecimal score;

    @Schema(description = "批阅评语")
    private String teacherComment;

    @Schema(description = "批改人姓名")
    private String gradedByName;

    @Schema(description = "提交时间")
    private LocalDateTime submitTime;

    @Schema(description = "批阅时间")
    private LocalDateTime gradeTime;

    @Schema(description = "提交文件列表")
    private List<BizSubmissionFile> files;
}
