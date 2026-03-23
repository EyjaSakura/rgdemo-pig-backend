package com.pig4cloud.pig.homework.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 作业发布表
 *
 * @author EyjaSakura
 * @date 2026-03-22 16:06:40
 */
@Data
@TableName("biz_homework")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "作业发布表")
public class HomeworkEntity extends Model<HomeworkEntity> {


	/**
	* 作业ID
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="作业ID")
    private Long homeworkId;

	/**
	* 所属课程ID
	*/
    @Schema(description="所属课程ID")
    private Long courseId;

	/**
	* 作业名
	*/
    @Schema(description="作业名")
    private String title;

	/**
	* 作业描述
	*/
    @Schema(description="作业描述")
    private String description;

	/**
	* 教师上传的附件地址
	*/
    @Schema(description="教师上传的附件地址")
    private String attachmentUrl;

	/**
	* 发布时间
	*/
    @Schema(description="发布时间")
    private LocalDateTime publishTime;

	/**
	* 提交截止时间
	*/
    @Schema(description="提交截止时间")
    private LocalDateTime deadline;

	/**
	* 逻辑删除标记
	*/
    @TableLogic
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="逻辑删除标记")
    private String delFlag;
}
