package com.pig4cloud.pig.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TempDTO {

	@Schema(description = "学号/教工号")
	private String username;

	@Schema(description = "密码")
	private String password;
}
