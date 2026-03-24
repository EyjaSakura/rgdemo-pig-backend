package com.pig4cloud.pig.homework.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作业提交与批阅表
 *
 * @author EyjaSakura
 * @date 2026-03-22 16:11:52
 */
@Data
@TableName("biz_homework_submission")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "作业提交与批阅表")
public class HomeworkSubmissionEntity extends Model<HomeworkSubmissionEntity> {


	/**
	* 提交记录ID
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="提交记录ID")
    private Long submissionId;

	/**
	* 关联的作业ID
	*/
    @Schema(description="关联的作业ID")
    private Long homeworkId;

	/**
	* 提交学生ID
	*/
    @Schema(description="提交学生ID")
    private Long studentId;

	/**
	 * 学生提交的正文内容
	 */
	@Schema(description = "学生提交的正文内容")
	private String submitContent;

	/**
	* 学生上传的作业文件URL
	*/
    @Schema(description="学生上传的作业文件URL")
    private String fileUrl;

	/**
	 * 教师批阅评语
	 */
	@Schema(description = "教师批阅评语")
	private String teacherComment;

	/**
	* 教师打分
	*/
    @Schema(description="教师打分")
    private BigDecimal score;

	/**
	* 状态 (0未交 1已交待批 2已批阅)
	*/
    @Schema(description="状态 (0未交 1已交待批 2已批阅)")
    private String status;

	/**
	* 实际提交时间
	*/
    @Schema(description="实际提交时间")
    private LocalDateTime submitTime;

	/**
	 * 创建时间
	 */
	@Schema(description = "创建时间")
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	/**
	* 教师批阅时间
	*/
	@TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description="教师批阅时间")
    private LocalDateTime updateTime;

	/**
	* 逻辑删除标记
	*/
    @TableLogic
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="逻辑删除标记")
    private String delFlag;

}
