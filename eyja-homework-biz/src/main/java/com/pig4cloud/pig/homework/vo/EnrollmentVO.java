package com.pig4cloud.pig.homework.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 选课记录 VO（含学生/课程信息）
 */
@Data
@Schema(description = "选课记录详情")
public class EnrollmentVO {

    @Schema(description = "选课记录ID")
    private Long id;

    @Schema(description = "任课ID")
    private Long teachingId;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "学生账号（学号）")
    private String studentUsername;

    @Schema(description = "课程号")
    private String courseCode;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "学分")
    private BigDecimal credit;

    @Schema(description = "教学年份")
    private Integer teachYear;

    @Schema(description = "课序号")
    private String classNo;

    @Schema(description = "教师姓名")
    private String teacherName;

    @Schema(description = "选课时间")
    private LocalDateTime enrolledAt;

    @Schema(description = "是否在学(1是 0已退)")
    private Integer isActive;

    @Schema(description = "学习进度(0-100)")
    private Integer progress;
}
