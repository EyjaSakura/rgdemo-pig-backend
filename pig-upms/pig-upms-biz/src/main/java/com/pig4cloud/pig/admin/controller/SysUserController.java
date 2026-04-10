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

package com.pig4cloud.pig.admin.controller;

import org.springframework.beans.BeanUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.admin.api.dto.*;
import com.pig4cloud.pig.admin.api.entity.SysUser;
import com.pig4cloud.pig.admin.api.vo.UserCenterVO;
import com.pig4cloud.pig.admin.service.SysUserService;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import com.pig4cloud.pig.common.security.annotation.Inner;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.pig4cloud.plugin.excel.annotation.RequestExcel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理控制器
 *
 * @author lengleng
 * @date 2025/05/30
 */
@RestController
@AllArgsConstructor
@RequestMapping("/user")
@Tag(description = "user", name = "用户管理模块")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysUserController {

	private final SysUserService userService;

	/**
	 * 查询用户信息
	 * @param userDTO 用户信息查询参数
	 * @return 包含用户信息的R对象
	 */
	@Inner
	@GetMapping(value = { "/info/query" })
	@Operation(summary = "查询用户信息", description = "查询用户信息")
	public R info(UserDTO userDTO) {
		return userService.getUserInfo(userDTO);
	}

	/**
	 * 获取当前登录用户的全部信息
	 * @return 包含用户信息的响应结果
	 */
	@GetMapping("/info")
	@Operation(summary = "获取当前登录用户的全部信息", description = "获取当前登录用户的全部信息")
	public R info() {
		String username = SecurityUtils.getUser().getUsername();
		UserDTO userDTO = new UserDTO();
		userDTO.setUsername(username);
		// 获取用户信息，不返回数据库密码字段
		R<UserInfo> userInfoR = userService.getUserInfo(userDTO);
		if (userInfoR.getData() != null) {
			userInfoR.getData().setPassword(null);
		}
		return userInfoR;
	}

	/**
	 * 通过ID查询用户信息
	 * @param id 用户ID
	 * @return 包含用户信息的响应对象
	 */
	@GetMapping("/details/{id}")
	@Operation(summary = "通过ID查询用户信息", description = "通过ID查询用户信息")
	public R user(@PathVariable Long id) {
		return R.ok(userService.getUserById(id));
	}

	/**
	 * 查询用户详细信息
	 * @param dto 用户查询条件对象
	 * @return 包含查询结果的响应对象，用户不存在时返回null
	 */
	@Inner(value = false)
	@PostMapping("/details")
	@Operation(summary = "查询用户详细信息", description = "查询用户详细信息")
	public R getDetails(@RequestBody UserDetailsDTO dto) {
		UserDTO userDTO = new UserDTO();
		BeanUtils.copyProperties(dto, userDTO);
		// 直接返回 getUserInfo 的结果，不要再包一层 R.ok()
		// getUserInfo 内部已处理：用户存在返回 R.ok(userInfo)，不存在返回 R.failed()
		return userService.getUserInfo(userDTO);
	}

	/**
	 * 删除用户信息
	 * @param ids 用户ID数组
	 * @return 操作结果
	 */
	@DeleteMapping
	@HasPermission("sys_user_del")
	@Operation(summary = "根据ID删除用户", description = "根据ID删除用户")
	public R userDel(@RequestBody Long[] ids) {
		return R.ok(userService.removeUserByIds(ids));
	}

	@PostMapping
	@HasPermission("sys_user_add")
	@Operation(summary = "添加用户", description = "添加用户")
	public R saveUser(@RequestBody UserDTO userDto) {
		return R.ok(userService.saveUser(userDto));
	}

	@PutMapping
	@HasPermission("sys_user_edit")
	@Operation(summary = "更新用户信息", description = "更新用户信息")
	public R updateUser(@Valid @RequestBody UserDTO userDto) {
		return R.ok(userService.updateUser(userDto));
	}

	/**
	 * 分页查询用户
	 * @param page 参数集
	 * @param userDTO 查询参数列表
	 * @return 用户集合
	 */
	@GetMapping("/page")
	@Operation(summary = "分页查询用户", description = "分页查询用户")
	public R getUserPage(@ParameterObject Page page, @ParameterObject UserDTO userDTO) {
		return R.ok(userService.getUsersWithRolePage(page, userDTO));
	}

	// 这个不是框架原有的
	// 按 userType / deptId 轻量查询用户列表（用于创建任课时选择教师下拉框等场景）

	@GetMapping("/list")
	@Operation(summary = "按条件查询用户列表（轻量）", description = "支持 userType、deptId 过滤，返回 id+name+username，用于下拉选择")
	public R listUsers(@ParameterObject UserDTO userDTO) {
		return R.ok(userService.list(com.baomidou.mybatisplus.core.toolkit.Wrappers.<com.pig4cloud.pig.admin.api.entity.SysUser>lambdaQuery()
				.eq(userDTO.getUserType() != null, com.pig4cloud.pig.admin.api.entity.SysUser::getUserType, userDTO.getUserType())
				.eq(userDTO.getDeptId() != null, com.pig4cloud.pig.admin.api.entity.SysUser::getDeptId, userDTO.getDeptId())
				.select(com.pig4cloud.pig.admin.api.entity.SysUser::getUserId,
						com.pig4cloud.pig.admin.api.entity.SysUser::getName,
						com.pig4cloud.pig.admin.api.entity.SysUser::getUsername,
						com.pig4cloud.pig.admin.api.entity.SysUser::getUserType,
						com.pig4cloud.pig.admin.api.entity.SysUser::getDeptId)));
	}

	/**
	 * 修改个人信息
	 * @param userDto 用户信息传输对象
	 * @return 操作结果，成功返回success，失败返回false
	 */
	@PutMapping("/edit")
	@Operation(summary = "修改个人信息", description = "修改个人信息")
	public R updateUserInfo(@Valid @RequestBody UserDTO userDto) {
		return userService.updateUserInfo(userDto);
	}

	/**
	 * 锁定指定用户
	 * @param username 用户名
	 * @return 操作结果
	 */
	@PutMapping("/lock/{username}")
	@Operation(summary = "锁定指定用户", description = "锁定指定用户")
	public R lockUser(@PathVariable String username) {
		return userService.lockUser(username);
	}

	/**
	 * 修改当前用户密码
	 * @param userDto 用户数据传输对象，包含新密码等信息
	 * @return 操作结果
	 */
	@PutMapping("/password")
	@Operation(summary = "修改当前用户密码", description = "修改当前用户密码")
	// 加了Valid注解，使dto中正则校验生效
	public R password(@Valid @RequestBody UserDTO userDto) {
		String username = SecurityUtils.getUser().getUsername();
		userDto.setUsername(username);
		return userService.changePassword(userDto);
	}

	// 这个不是框架原有的
	// 修改个人信息（头像、个性签名）

	@PutMapping("/info")
	public R editInfo(@RequestBody UserProfileDTO profileDTO) {
		// 从 token 中获取当前登录用户的 ID
		Long userId = SecurityUtils.getUser().getId();

		// new 一个 SysUser 对象
		SysUser user = new SysUser();

		user.setUserId(userId);
		user.setAvatar(profileDTO.getAvatar());
		user.setSignature(profileDTO.getSignature());

		// 调用 Service 的自带方法直接更新数据库
		userService.updateById(user);

		return R.ok(null,"个人资料修改成功！");
	}

	// 这个不是框架原有的
	// 发送手机验证码（测试先直接发前端，跳过运营商）

	@Inner(value = false)
	@GetMapping("/send-code/{phone}")
	public R sendCode(@PathVariable String phone) {
		return userService.sendResetCode(phone);
	}

	// 这个不是框架原有的
	// 验证手机验证码并重置密码

	@Inner(value = false)
	@PutMapping("/reset-password")
	public R resetPassword(@Valid @RequestBody ResetPwdDTO dto) {
		return userService.resetPasswordByPhone(dto);
	}

	// 这个不是框架原有的
	// 获取个人中心信息

	@GetMapping("/center")
	@Operation(summary = "获取个人中心信息")
	public R<UserCenterVO> getUserCenter() {
		return R.ok(userService.getUserCenterInfo());
	}

	// 这个不是框架原有的
	// Excel批量导入学生

	@PostMapping("/import-student")
	@HasPermission("sys_student_import")
	@Operation(summary = "Excel批量导入学生", description = "Excel批量导入学生")
	public R importStudent(@RequestExcel List<StudentExcelDTO> excelVOList, BindingResult bindingResult) {
		return userService.importStudents(excelVOList, bindingResult);
	}

	// 这个不是框架原有的
	// Excel批量导入学生

	@PostMapping("/import-teacher")
	@HasPermission("sys_teacher_import")
	@Operation(summary = "Excel批量导入教师", description = "Excel批量导入教师")
	public R importTeacher(@RequestExcel List<TeacherExcelDTO> excelVOList, BindingResult bindingResult) {
		return userService.importTeachers(excelVOList, bindingResult);
	}

	// 这个不是框架原有的
	// 根据班级ID获取该班级所有学生ID
	// Inner内部调用，无需token

	@Inner
	@GetMapping("/student/ids/{deptId}")
	public R<List<Long>> getStudentIdsByDeptId(@PathVariable("deptId") Long deptId) {
		// 查出该班级所有学生
		List<SysUser> studentList = userService.list(Wrappers.<SysUser>lambdaQuery()
				.eq(SysUser::getDeptId, deptId)
				.eq(SysUser::getUserType, "stu"));

		// 提取出 ID 列表
		List<Long> ids = studentList.stream().map(SysUser::getUserId).collect(Collectors.toList());
		return R.ok(ids);
	}


	@PutMapping("/temp-password")
	public R tempPassword(@RequestBody TempDTO dto) {
		return userService.tempPassword(dto);
	}

}
