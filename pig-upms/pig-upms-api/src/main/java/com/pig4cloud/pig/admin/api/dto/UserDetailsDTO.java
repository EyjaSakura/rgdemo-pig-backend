
// EyjaSakura：这个不是框架原有
// 获取详细信息接收前端数据的dto

package com.pig4cloud.pig.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserDetailsDTO {
	@Schema(description = "用户ID")
	private Long userId;

	@Schema(description = "学号/教工号")
	private String username;

	@Schema(description = "手机号")
	private String phone;
}
