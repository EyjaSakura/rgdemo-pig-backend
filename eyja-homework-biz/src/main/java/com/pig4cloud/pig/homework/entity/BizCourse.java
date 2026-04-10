package com.pig4cloud.pig.homework.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 课程信息表
 *
 * @author EyjaSakura
 * @since 2026-04-08 20:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "课程信息表")
public class BizCourse implements Serializable {

    private static final long serialVersionUID = 1L;
    @Schema(description = "课程号（8位字符串，如 10010001）")
    @TableId(type = IdType.INPUT)
    private String courseCode;
    @Schema(description = "所属学院ID（关联 biz_college）")
    private Long collegeId;
    @Schema(description = "课程名")
    private String courseName;
    @Schema(description = "课程学分")
    private BigDecimal credit;
    @Schema(description = "课程总课时")
    private Integer totalHours;
    @Schema(description = "课程简介")
    private String description;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "修改时间")
    private Date updateTime;
    @Schema(description = "逻辑删除标记 0正常 1删除")
    private String delFlag;
}
