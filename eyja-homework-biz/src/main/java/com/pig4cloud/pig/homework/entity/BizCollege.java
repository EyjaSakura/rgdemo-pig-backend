package com.pig4cloud.pig.homework.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 学院信息表
 *
 * @author EyjaSakura
 * @since 2026-04-08 20:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "学院信息表")
public class BizCollege implements Serializable {

    private static final long serialVersionUID = 1L;
    @Schema(description = "学院ID")
    @TableId(type = IdType.AUTO)
    private Long collegeId;
    @Schema(description = "学院编号（如 CS、EE）")
    private String collegeCode;
    @Schema(description = "学院名称")
    private String collegeName;
    @Schema(description = "关联 sys_dept 中的学院层级节点（可选，用于层级查询）")
    private Long deptId;
    @Schema(description = "学院简介")
    private String description;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "修改时间")
    private Date updateTime;
    @Schema(description = "逻辑删除标记 0正常 1删除")
    private String delFlag;
}
