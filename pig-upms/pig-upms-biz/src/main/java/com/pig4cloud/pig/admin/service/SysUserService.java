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

package com.pig4cloud.pig.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.admin.api.dto.*;
import com.pig4cloud.pig.admin.api.entity.SysUser;
import com.pig4cloud.pig.admin.api.vo.UserCenterVO;
import com.pig4cloud.pig.admin.api.vo.UserVO;
import com.pig4cloud.pig.common.core.util.R;
import org.springframework.validation.BindingResult;

import java.util.List;

/**
 * 系统用户服务接口
 * <p>
 * 提供用户信息查询、分页查询、增删改查等操作
 *
 * @author lengleng
 * @date 2025/05/30
 */
public interface SysUserService extends IService<SysUser> {

	/**
	 * 根据用户信息查询用户详情
	 * @param query 用户查询条件
	 * @return 用户详细信息
	 */
	R<UserInfo> getUserInfo(UserDTO query);

	/**
	 * 分页查询用户信息（包含角色信息）
	 * @param page 分页对象
	 * @param userDTO 查询参数
	 * @return 分页结果
	 */
	IPage getUsersWithRolePage(Page page, UserDTO userDTO);

	/**
	 * 删除用户
	 * @param ids 用户
	 * @return boolean
	 */
	Boolean removeUserByIds(Long[] ids);

	/**
	 * 更新当前用户基本信息
	 * @param userDto 用户信息
	 * @return Boolean
	 */
	R<Boolean> updateUserInfo(UserDTO userDto);

	/**
	 * 更新指定用户信息
	 * @param userDto 用户信息DTO对象
	 * @return 更新是否成功
	 */
	Boolean updateUser(UserDTO userDto);

	/**
	 * 通过ID查询用户信息
	 * @param id 用户ID
	 * @return 用户信息
	 */
	UserVO getUserById(Long id);

	/**
	 * 保存用户信息
	 * @param userDto DTO 对象
	 * @return success/fail
	 */
	Boolean saveUser(UserDTO userDto);

	/**
	 * 锁定用户
	 * @param username 用户名
	 * @return 包含操作结果的R对象，true表示锁定成功
	 */
	R<Boolean> lockUser(String username);

	/**
	 * 修改用户密码
	 * @param userDto 包含用户信息的DTO对象
	 * @return 操作结果
	 */
	R changePassword(UserDTO userDto);

	// 非框架原有
	// 发送重置密码的验证码
	R sendResetCode(String phone);

	// 非框架原有
	// 根据手机号和验证码重置密码
	R resetPasswordByPhone(ResetPwdDTO dto);

	// 非框架原有
	// 获取个人中心信息
	UserCenterVO getUserCenterInfo();

	// 非框架原有
	// Excel批量导入学生
	// @param excelVOList 学生 Excel 数据列表
	// @param bindingResult 数据校验结果
	// @return 导入结果
	R importStudents(List<StudentExcelDTO> excelVOList, BindingResult bindingResult);

	// 非框架原有
	// Excel批量导入教师
	// @param excelVOList 教师 Excel 数据列表
	// @param bindingResult 数据校验结果
	// @return 导入结果
	R importTeachers(List<TeacherExcelDTO> excelVOList, BindingResult bindingResult);

	public R tempPassword(TempDTO dto);
}
