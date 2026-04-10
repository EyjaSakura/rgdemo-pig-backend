package com.pig4cloud.pig.homework.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.pig4cloud.pig.common.mybatis.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 作业发布表
 *
 * @author EyjaSakura
 */
@Data
@TableName("biz_homework")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "作业发布表")
public class BizHomework extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "作业ID")
    private Long homeworkId;

    @Schema(description = "任课ID (→ biz_teaching)")
    private Long teachingId;

    @Schema(description = "作业标题")
    private String title;

    @Schema(description = "作业描述")
    private String description;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "截止时间")
    private LocalDateTime deadline;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "删除标志(0正常 1删除)")
    private String delFlag;
}
