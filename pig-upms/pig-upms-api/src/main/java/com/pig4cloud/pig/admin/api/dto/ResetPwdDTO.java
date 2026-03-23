
// EyjaSakura：这个不是框架原有
// 重置密码的dto

package com.pig4cloud.pig.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPwdDTO {
	@Schema(description = "手机号")
	private String phone;

	@Schema(description = "验证码")
	private String code;

	@Schema(description = "新密码")
	@Size(max = 20, message = "密码长度不能超过20个字符")
	// 正则匹配（不是框架原有）
	@Pattern(
			regexp = "^(?![a-zA-Z]+$)(?![0-9]+$)(?![^a-zA-Z0-9]+$).{1,20}$",
			message = "密码需至少包含字母，字符，数字中的两种以确保安全性，并且不超过20个字符"
	)
	private String newPassword;
}
