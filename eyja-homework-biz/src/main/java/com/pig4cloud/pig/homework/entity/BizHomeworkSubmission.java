package com.pig4cloud.pig.homework.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作业提交与批阅
 *
 * @author EyjaSakura
 */
@Data
@TableName("biz_homework_submission")
@Schema(description = "作业提交与批阅")
public class BizHomeworkSubmission {

    @TableId(type = IdType.AUTO)
    @Schema(description = "提交记录ID")
    private Long submissionId;

    @Schema(description = "关联作业ID (→ biz_homework)")
    private Long homeworkId;

    @Schema(description = "提交学生ID (→ sys_user)")
    private Long studentId;

    @Schema(description = "学生提交的正文内容")
    private String submitContent;

    @Schema(description = "状态 (0未交 1已交待批 2已批阅)")
    private String status;

    @Schema(description = "教师批阅评语")
    private String teacherComment;

    @Schema(description = "教师打分")
    private BigDecimal score;

    @Schema(description = "批阅人（教师或助教用户名）")
    private String gradeBy;

    @Schema(description = "批阅时间")
    private LocalDateTime gradeTime;

    @Schema(description = "实际提交时间")
    private LocalDateTime submitTime;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "删除标志(0正常 1删除)")
    private String delFlag;
}
