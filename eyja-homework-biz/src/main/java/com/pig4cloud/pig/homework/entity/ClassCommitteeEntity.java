package com.pig4cloud.pig.homework.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 班委职务分配表
 *
 * @author EyjaSakura
 * @date 2026-03-22 12:34:36
 */
@Data
@TableName("biz_class_committee")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "班委职务分配表")
public class ClassCommitteeEntity extends Model<ClassCommitteeEntity> {


	/**
	* 主键
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="主键")
    private Long id;

	/**
	* 班级ID (关联 sys_dept.dept_id)
	*/
    @Schema(description="班级ID (关联 sys_dept.dept_id)")
    private Long deptId;

	/**
	* 学生ID (关联 sys_user.user_id)
	*/
    @Schema(description="学生ID (关联 sys_user.user_id)")
    private Long studentId;

	/**
	* 职务名称(如:班长,心理委员等)
	*/
    @Schema(description="职务名称(如:班长,心理委员等)")
    private String roleName;
}
