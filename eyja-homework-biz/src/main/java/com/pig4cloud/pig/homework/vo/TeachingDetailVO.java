package com.pig4cloud.pig.homework.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 任课详情 VO（联查课程名、教师名、已选人数）
 */
@Data
@Schema(description = "任课详情")
public class TeachingDetailVO {

    @Schema(description = "任课ID")
    private Long teachingId;

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

    @Schema(description = "任课教师ID")
    private Long teacherId;

    @Schema(description = "教师姓名")
    private String teacherName;

    @Schema(description = "教师账号")
    private String teacherUsername;

    @Schema(description = "上课时间与地点")
    private String timePlace;

    @Schema(description = "选课人数上限")
    private Integer maxStudents;

    @Schema(description = "当前已选人数")
    private Integer enrolledCount;

    @Schema(description = "任课状态 (0未开始 1进行中 2已结束)")
    private String status;

    @Schema(description = "学院名称")
    private String collegeName;

    @Schema(description = "助教ID（若有）")
    private Long assistantId;

    @Schema(description = "助教姓名（若有）")
    private String assistantName;
}
