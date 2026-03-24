
// EyjaSakura：这个不是框架原有
// excel导入教师信息的dto

package com.pig4cloud.pig.admin.api.dto;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.pig4cloud.plugin.excel.annotation.DictTypeProperty;
import com.pig4cloud.plugin.excel.annotation.ExcelLine;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class TeacherExcelDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	// 记录报错行数
	@ExcelLine
	@ExcelIgnore
	private Long lineNum;

	@NotBlank(message = "教工号不能为空")
	@ExcelProperty("教工号")
	private String username;

	@NotBlank(message = "姓名不能为空")
	@ExcelProperty("姓名")
	private String name;

	@NotBlank(message = "性别不能为空")
	@ExcelProperty("性别")
	@DictTypeProperty("sys_user_sex")
	private String sex;

	@NotBlank(message = "邮箱不能为空")
	@ExcelProperty("邮箱")
	private String email;

	@NotBlank(message = "手机号不能为空")
	@ExcelProperty("手机号")
	private String phone;

	@NotBlank(message = "学院不能为空")
	@ExcelProperty("学院")
	private String collegeName;

}
