package com.pig4cloud.pig.homework.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 课件管理表
 *
 * @author EyjaSakura
 * @date 2026-03-22 13:43:35
 */
@Data
@TableName("biz_courseware")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "课件管理表")
public class CoursewareEntity extends Model<CoursewareEntity> {


	/**
	* 课件ID
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="课件ID")
    private Long coursewareId;

	/**
	* 所属课程ID
	*/
    @Schema(description="所属课程ID")
    private Long courseId;

	/**
	* 文件夹/分组名称
	*/
    @Schema(description="文件夹/分组名称")
    private String folderName;

	/**
	* 课件命名
	*/
    @Schema(description="课件命名")
    private String title;

	/**
	* 课件文件下载地址/OSS路径
	*/
    @Schema(description="课件文件下载地址/OSS路径")
    private String fileUrl;

	/**
	* 排序权重(用于调整顺序)
	*/
    @Schema(description="排序权重(用于调整顺序)")
    private Integer sortOrder;

	/**
	* 上传时间
	*/
    @Schema(description="上传时间")
    private LocalDateTime uploadTime;

	/**
	* 逻辑删除标记
	*/
    @TableLogic
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="逻辑删除标记")
    private String delFlag;
}
