package com.pig4cloud.pig.homework.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 课程信息表
 *
 * @author EyjaSakura
 * @date 2026-03-22 13:01:47
 */
@Data
@TableName("biz_course")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "课程信息表")
public class CourseEntity extends Model<CourseEntity> {


	/**
	* 课程ID
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="课程ID")
    private Long courseId;

	/**
	* 课程名
	*/
    @Schema(description="课程名")
    private String courseName;

	/**
	* 课程号
	*/
    @Schema(description="课程号")
    private String courseCode;

	/**
	* 课程学分
	*/
    @Schema(description="课程学分")
    private Integer credit;

	/**
	* 上课时间与地点
	*/
    @Schema(description="上课时间与地点")
    private String timePlace;

	/**
	* 是否必修 (0选修 1必修)
	*/
    @Schema(description="是否必修 (0选修 1必修)")
    private Integer isRequired;

	/**
	* 授课教师ID (关联 sys_user)
	*/
    @Schema(description="授课教师ID (关联 sys_user)")
    private Long teacherId;

	/**
	* 状态 (0正常 1已结课)
	*/
    @Schema(description="状态 (0正常 1已结课)")
    private Integer status;

	/**
	* 创建时间
	*/
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="创建时间")
    private LocalDateTime createTime;

	/**
	* 更新时间
	*/
	@TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description="更新时间")
    private LocalDateTime updateTime;

	/**
	* 逻辑删除标记
	*/
    @TableLogic
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="逻辑删除标记")
    private String delFlag;
}
