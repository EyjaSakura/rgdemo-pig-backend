
// EyjaSakura：这个不是框架原有
// 个人资料的dto

package com.pig4cloud.pig.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserProfileDTO {
	@Schema(description = "头像图片URL")
	private String avatar;

	@Schema(description = "个性签名")
	private String signature;
}
