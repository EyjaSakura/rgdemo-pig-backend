package com.pig4cloud.pig.admin.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "个人中心信息")
public class UserCenterVO {
	/**
	 * 姓名
	 */
	@Schema(description = "姓名")
	private String name;

	/**
	 * 头像URL
	 */
	@Schema(description = "头像URL")
	private String avatar;

	/**
	 * 个性签名
	 */
	@Schema(description = "个性签名")
	private String signature;

	/**
	 * 手机号
	 */
	@Schema(description = "手机号")
	private String phone;

	/**
	 * 学号/教职工号
	 */
	@Schema(description = "学号/教职工号")
	private String username;


}
