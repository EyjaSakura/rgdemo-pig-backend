package com.pig4cloud.pig.homework.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 必修课-班级自动关联表
 *
 * @author EyjaSakura
 * @date 2026-03-22 13:38:04
 */
@Data
@TableName("biz_course_class")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "必修课-班级自动关联表")
public class CourseClassEntity extends Model<CourseClassEntity> {


	/**
	* 主键
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="主键")
    private Long id;

	/**
	* 课程ID
	*/
    @Schema(description="课程ID")
    private Long courseId;

	/**
	* 班级ID
	*/
    @Schema(description="班级ID")
    private Long deptId;
}
