/*
 *
 *      Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the pig4cloud.com developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: lengleng (wangiegie@gmail.com)
 *
 */

package com.pig4cloud.pig.admin.api.feign;

import com.pig4cloud.pig.admin.api.dto.UserDTO;
import com.pig4cloud.pig.admin.api.dto.UserInfo;
import com.pig4cloud.pig.common.core.constant.SecurityConstants;
import com.pig4cloud.pig.common.core.constant.ServiceNameConstants;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.feign.annotation.NoToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * 远程用户服务接口：提供用户信息查询功能
 *
 * @author lengleng
 * @date 2025/05/30
 */
@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.UPMS_SERVICE)
public interface RemoteUserService {

	/**
	 * (未登录状态调用，需要加 @NoToken) 通过用户名查询用户、角色信息
	 * @param user 用户查询对象
	 * @return R
	 */
	@NoToken
	@GetMapping("/user/info/query")
	R<UserInfo> info(@SpringQueryMap UserDTO user);

	/**
	 * 非框架原有
	 * 根据班级 ID 获取所有学生的 ID 列表 (内部调用)
	 *
	 * @param deptId 班级ID
	 * @param from   调用来源 (配合 @Inner 使用)
	 * @return R<List<Long>>
	 */
	@NoToken  // 告诉拦截器不用去找token
	@GetMapping("/user/student/ids/{deptId}")
	R<List<Long>> getStudentIdsByDeptId(@PathVariable("deptId") Long deptId, @RequestHeader(SecurityConstants.FROM) String from);

}
