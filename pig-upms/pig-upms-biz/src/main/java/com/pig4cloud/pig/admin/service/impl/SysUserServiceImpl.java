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

package com.pig4cloud.pig.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.admin.api.dto.*;
import com.pig4cloud.pig.admin.api.entity.*;
import com.pig4cloud.pig.admin.api.util.ParamResolver;
import com.pig4cloud.pig.admin.api.vo.UserCenterVO;
import com.pig4cloud.pig.admin.api.vo.UserVO;
import com.pig4cloud.pig.admin.mapper.SysUserMapper;
import com.pig4cloud.pig.admin.mapper.SysUserRoleMapper;
import com.pig4cloud.pig.admin.service.SysDeptService;
import com.pig4cloud.pig.admin.service.SysMenuService;
import com.pig4cloud.pig.admin.service.SysRoleService;
import com.pig4cloud.pig.admin.service.SysUserService;
import com.pig4cloud.pig.common.core.constant.CacheConstants;
import com.pig4cloud.pig.common.core.constant.CommonConstants;
import com.pig4cloud.pig.common.core.exception.ErrorCodes;
import com.pig4cloud.pig.common.core.util.MsgUtils;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.pig4cloud.plugin.excel.vo.ErrorMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 系统用户服务实现类
 *
 * @author lengleng
 * @date 2025/05/30
 */
@Slf4j
@Service
@AllArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

	// redis（非框架原有）
	private RedisTemplate<String, String> redisTemplate;

	private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

	private final SysMenuService sysMenuService;

	private final SysRoleService sysRoleService;

	private final SysDeptService sysDeptService;

	private final SysUserRoleMapper sysUserRoleMapper;

	private final CacheManager cacheManager;

	/**
	 * 保存用户信息
	 * @param userDto 用户数据传输对象
	 * @return 操作是否成功
	 * @throws Exception 事务回滚时抛出异常
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean saveUser(UserDTO userDto) {
		SysUser sysUser = new SysUser();
		BeanUtils.copyProperties(userDto, sysUser);
		// 空字符串转null，避免整数字段写入空字符串报错（如 enroll_year）
		if (StrUtil.isBlank(sysUser.getEnrollYear())) {
			sysUser.setEnrollYear(null);
		}
		sysUser.setDelFlag(CommonConstants.STATUS_NORMAL);
		sysUser.setCreateBy(userDto.getUsername());
		sysUser.setPassword(ENCODER.encode(userDto.getPassword()));
		baseMapper.insert(sysUser);

		// 如果角色为空，赋默认角色
		if (CollUtil.isEmpty(userDto.getRole())) {
			// 获取默认角色编码
			String defaultRole = ParamResolver.getStr("USER_DEFAULT_ROLE");
			// 默认角色
			SysRole sysRole = sysRoleService
				.getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleCode, defaultRole));
			userDto.setRole(Collections.singletonList(sysRole.getRoleId()));
		}

		// 插入用户角色关系表
		userDto.getRole().forEach(roleId -> {
			SysUserRole userRole = new SysUserRole();
			userRole.setUserId(sysUser.getUserId());
			userRole.setRoleId(roleId);
			sysUserRoleMapper.insert(userRole);
		});
		return Boolean.TRUE;
	}

	/**
	 * 查询用户全部信息，包括角色和权限
	 * @param query 用户查询条件
	 * @return 包含用户角色和权限的用户信息对象
	 */
	@Override
	public R<UserInfo> getUserInfo(UserDTO query) {
		UserVO dbUser = baseMapper.getUser(query);

		if (dbUser == null) {
			return R.failed(MsgUtils.getMessage(ErrorCodes.SYS_USER_USERINFO_EMPTY, query.getUsername()));
		}

		UserInfo userInfo = new UserInfo();
		BeanUtils.copyProperties(dbUser, userInfo);
		// 设置权限列表（menu.permission）
		List<String> permissions = dbUser.getRoleList()
			.stream()
			.map(SysRole::getRoleId)
			.flatMap(roleId -> sysMenuService.findMenuByRoleId(roleId).stream())
			.filter(menu -> StrUtil.isNotEmpty(menu.getPermission()))
			.map(SysMenu::getPermission)
			.toList();
		userInfo.setPermissions(permissions);

		userInfo.setFullName(sysDeptService.getFullDeptPathName(dbUser.getDept().getDeptId()));

		return R.ok(userInfo);
	}

	/**
	 * 分页查询用户信息（包含角色信息）
	 * @param page 分页对象
	 * @param userDTO 查询参数
	 * @return 包含用户和角色信息的分页结果
	 */
	@Override
	public IPage getUsersWithRolePage(Page page, UserDTO userDTO) {
		return baseMapper.getUsersPage(page, userDTO);
	}

	/**
	 * 通过ID查询用户信息
	 * @param id 用户ID
	 * @return 用户信息VO对象
	 */
	@Override
	public UserVO getUserById(Long id) {
		UserDTO query = new UserDTO();
		query.setUserId(id);
		return baseMapper.getUser(query);
	}

	/**
	 * 根据用户ID列表删除用户及相关缓存
	 * @param ids 用户ID数组
	 * @return 删除成功返回true
	 * @throws Exception 事务回滚时抛出异常
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean removeUserByIds(Long[] ids) {
		List<Long> idList = CollUtil.toList(ids);
		// 删除 spring cache
		Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
		baseMapper.selectByIds(idList).forEach(user -> cache.evictIfPresent(user.getUsername()));

		sysUserRoleMapper.delete(Wrappers.<SysUserRole>lambdaQuery().in(SysUserRole::getUserId, idList));
		this.removeBatchByIds(idList);
		return Boolean.TRUE;
	}

	/**
	 * 更新用户信息
	 * @param userDto 用户数据传输对象
	 * @return 操作结果，包含更新是否成功
	 */
	@Override
	@CacheEvict(value = CacheConstants.USER_DETAILS, key = "#userDto.username")
	public R<Boolean> updateUserInfo(UserDTO userDto) {
		SysUser sysUser = new SysUser();
		sysUser.setPhone(userDto.getPhone());
		sysUser.setUserId(SecurityUtils.getUser().getId());
		sysUser.setAvatar(userDto.getAvatar());
		sysUser.setName(userDto.getName());
		return R.ok(this.updateById(sysUser));
	}

	/**
	 * 更新用户信息
	 * @param userDto 用户数据传输对象，包含需要更新的用户信息
	 * @return 更新成功返回true
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = CacheConstants.USER_DETAILS, key = "#userDto.username")
	public Boolean updateUser(UserDTO userDto) {
		// 更新用户表信息
		SysUser sysUser = new SysUser();
		BeanUtils.copyProperties(userDto, sysUser);
		// updateTime 已通过 @TableField(fill=UPDATE) 自动填充，无需手动设置
		if (StrUtil.isNotBlank(userDto.getPassword())) {
			sysUser.setPassword(ENCODER.encode(userDto.getPassword()));
		}
		this.updateById(sysUser);

		// 更新用户角色表
		if (Objects.nonNull(userDto.getRole())) {
			// 删除用户角色关系
			sysUserRoleMapper
				.delete(Wrappers.<SysUserRole>lambdaQuery().eq(SysUserRole::getUserId, userDto.getUserId()));
			userDto.getRole().forEach(roleId -> {
				SysUserRole userRole = new SysUserRole();
				userRole.setUserId(sysUser.getUserId());
				userRole.setRoleId(roleId);
				sysUserRoleMapper.insert(userRole);
			});
		}

		return Boolean.TRUE;
	}

	/**
	 * 锁定用户
	 * @param username 用户名
	 * @return 操作结果，包含是否成功的信息
	 */
	@Override
	@CacheEvict(value = CacheConstants.USER_DETAILS, key = "#username")
	public R<Boolean> lockUser(String username) {
		SysUser sysUser = baseMapper.selectOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username));

		if (Objects.nonNull(sysUser)) {
			sysUser.setLockFlag(CommonConstants.STATUS_LOCK);
			baseMapper.updateById(sysUser);
		}
		return R.ok();
	}

	/**
	 * 修改用户密码
	 * @param userDto 用户信息传输对象，包含用户名、原密码和新密码
	 * @return 操作结果，成功返回R.ok()，失败返回错误信息
	 * @CacheEvict 清除用户详情缓存
	 */
	@Override
	@CacheEvict(value = CacheConstants.USER_DETAILS, key = "#userDto.username")
	public R changePassword(UserDTO userDto) {
		SysUser sysUser = baseMapper.selectById(SecurityUtils.getUser().getId());
		if (Objects.isNull(sysUser)) {
			return R.failed("用户不存在");
		}

		if (StrUtil.isEmpty(userDto.getNewpassword1())) {
			return R.failed("新密码不能为空");
		}

		// 新密码不能与旧密码相同
		if (ENCODER.matches(userDto.getNewpassword1(), sysUser.getPassword())) {
			return R.failed("新密码不能与旧密码相同");
		}

		String password = ENCODER.encode(userDto.getNewpassword1());

		this.update(Wrappers.<SysUser>lambdaUpdate()
			.set(SysUser::getPassword, password)
			.eq(SysUser::getUserId, sysUser.getUserId()));
		return R.ok();
	}

	// 发送验证码（非框架原有）
	@Override
	public R sendResetCode(String phone) {
		SysUser sysUser = baseMapper.selectOne(Wrappers.<SysUser>query().lambda().eq(SysUser::getPhone, phone));
		if (sysUser == null) {
			return R.failed(null, "手机号错误，该用户不存在！");
		}

		String code = RandomUtil.randomNumbers(6);
		String redisKey = "RESET_PWD:" + phone;
		redisTemplate.opsForValue().set(redisKey, code, 5, TimeUnit.MINUTES);

		// 生产环境替换为真实短信服务
		log.info("====== 模拟发送短信 ====== 手机号: {}, 验证码: {}", phone, code);

		// 测试阶段：直接在响应中返回验证码（上线前务必移除 code 字段，仅返回成功提示）
		return R.ok(code, "验证码发送成功，请注意查收！");
	}

	// 验证验证码并重置密码（非框架原有）
	@Override
	public R resetPasswordByPhone(ResetPwdDTO dto){
		String redisKey = "RESET_PWD:" + dto.getPhone();
		String storedCode = redisTemplate.opsForValue().get(redisKey);

		if (storedCode == null) {
			return R.failed("验证码已过期，请重新获取");
		}
		if (!storedCode.equals(dto.getCode())) {
			return R.failed("验证码错误");
		}

		SysUser sysUser = baseMapper.selectOne(Wrappers.<SysUser>query().lambda().eq(SysUser::getPhone, dto.getPhone()));
		if (sysUser == null) {
			return R.failed("用户不存在");
		}

		// 新密码不能与旧密码相同
		if (ENCODER.matches(dto.getNewPassword(), sysUser.getPassword())) {
			return R.failed("新密码不能与旧密码相同");
		}

		String password = ENCODER.encode(dto.getNewPassword());

		this.update(Wrappers.<SysUser>lambdaUpdate()
				.set(SysUser::getPassword, password)
				.eq(SysUser::getUserId, sysUser.getUserId()));

		// 清除redis验证码
		redisTemplate.delete(redisKey);

		return R.ok(null, "密码重置成功，请使用新密码登录！");
	}

	// 非框架原有
	// 获取个人中心信息
	@Override
	public UserCenterVO getUserCenterInfo(){
		// 查询数据库完整的用户实体
		SysUser sysUser = baseMapper.selectById(SecurityUtils.getUser().getId());

		// 将 Entity 转换为 VO
		UserCenterVO vo = new UserCenterVO();
		vo.setAvatar(sysUser.getAvatar());
		vo.setName(sysUser.getName());
		vo.setUsername(sysUser.getUsername());
		vo.setPhone(sysUser.getPhone());
		vo.setSignature(sysUser.getSignature());
		vo.setUserType(sysUser.getUserType());
		vo.setDeptId(sysUser.getDeptId());

		return vo;
	}

	// 非框架原有
	// Excel批量导入学生

	@Override
	public R importStudents(List<StudentExcelDTO> excelVOList, BindingResult bindingResult) {
		// 获取基础校验的报错信息
		List<ErrorMessage> errorMessageList = (List<ErrorMessage>) bindingResult.getTarget();

		// 提前查出全校的部门树和角色表
		List<SysDept> allDeptList = sysDeptService.list();
		SysRole studentRole = sysRoleService.getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleCode, "ROLE_STUDENT"));

		if (studentRole == null) {
			return R.failed("系统未配置学生角色，请联系管理员核对角色编码！");
		}

		// 开始遍历教务老师上传的 Excel 数据
		for (StudentExcelDTO excel : excelVOList) {
			Set<String> errorMsg = new HashSet<>();

			// 查重：判断学号是否已存在
			long count = this.count(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, excel.getUsername()));
			if (count > 0) {
				errorMsg.add("学号 [" + excel.getUsername() + "] 已存在");
			}

			// 找班级：拿着 Excel 里的汉字去找 ID。
			// deptCategory 必须是 '4'（层级：0学校/1职能/2学院/3专业/4班级）
			Optional<SysDept> classDept = allDeptList.stream()
					.filter(dept -> excel.getClassName().equals(dept.getName()) && "4".equals(dept.getDeptCategory()))
					.findFirst();

			if (classDept.isEmpty()) {
				errorMsg.add("找不到班级 [" + excel.getClassName() + "]，请检查名称是否完整或是否为班级层次");
			}

			// 校验完毕，拼装对象并入库
			if (CollUtil.isEmpty(errorMsg)) {
				UserDTO userDTO = new UserDTO();
				userDTO.setUsername(excel.getUsername()); // 学号
				userDTO.setName(excel.getName());         // 姓名
				userDTO.setPhone(excel.getPhone());       // 手机号
				userDTO.setEnrollYear(excel.getEnrollYear()); // 入学年份

				// 核心身份
				userDTO.setDeptId(classDept.get().getDeptId()); // 关联真实的班级 ID
				userDTO.setUserType("stu");                       // 身份标识为学生 (adm管理 tch教师 stu学生)
				userDTO.setPassword("666666");                  // 强制初始化密码
				userDTO.setRole(Collections.singletonList(studentRole.getRoleId())); // 绑定学生角色

				// 调用原生的保存逻辑
				this.saveUser(userDTO);
			} else {
				// 如果有错误，记录这一行的报错信息
				errorMessageList.add(new ErrorMessage(excel.getLineNum(), errorMsg));
			}
		}

		// 汇总报错信息返回给前端
		if (CollUtil.isNotEmpty(errorMessageList)) {
			return R.failed(errorMessageList);
		}
		return R.ok(null, "学生批量导入成功！");
	}

	// 非框架原有
	// Excel批量导入教师

	@Override
	public R importTeachers(List<TeacherExcelDTO> excelVOList, BindingResult bindingResult) {
		// 获取基础校验的报错信息
		List<ErrorMessage> errorMessageList = (List<ErrorMessage>) bindingResult.getTarget();

		// 提前查出全校的部门树和角色表
		List<SysDept> allDeptList = sysDeptService.list();
		SysRole teacherRole = sysRoleService.getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleCode, "ROLE_TEACHER"));

		if (teacherRole == null) {
			return R.failed("系统未配置教师角色，请联系管理员核对角色编码！");
		}

		// 开始遍历教务老师上传的 Excel 数据
		for (TeacherExcelDTO excel : excelVOList) {
			Set<String> errorMsg = new HashSet<>();

			// 查重：判断教工号是否已存在
			long count = this.count(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, excel.getUsername()));
			if (count > 0) {
				errorMsg.add("教工号 [" + excel.getUsername() + "] 已存在");
			}

			// 找班级：拿着 Excel 里的汉字去找 ID。
			// deptCategory 必须是 '2'（层级：0学校/1职能/2学院/3专业/4班级）
			Optional<SysDept> collegeDept = allDeptList.stream()
					.filter(dept -> excel.getCollegeName().equals(dept.getName()) && "2".equals(dept.getDeptCategory()))
					.findFirst();

			if (collegeDept.isEmpty()) {
				errorMsg.add("找不到学院 [" + excel.getCollegeName() + "]，请检查名称是否完整或是否为学院层次");
			}

			// 校验完毕，拼装对象并入库
			if (CollUtil.isEmpty(errorMsg)) {
				UserDTO userDTO = new UserDTO();
				userDTO.setUsername(excel.getUsername()); // 教工号
				userDTO.setName(excel.getName());         // 姓名
				userDTO.setPhone(excel.getPhone());       // 手机号

				// 核心身份
				userDTO.setDeptId(collegeDept.get().getDeptId()); // 关联真实的学院 ID
				userDTO.setUserType("tch");                       // 身份标识为教师 (adm管理 tch教师 stu学生)
				userDTO.setPassword("777777");                  // 强制初始化密码
				userDTO.setRole(Collections.singletonList(teacherRole.getRoleId())); // 绑定学生角色

				// 调用原生的保存逻辑
				this.saveUser(userDTO);
			} else {
				// 如果有错误，记录这一行的报错信息
				errorMessageList.add(new ErrorMessage(excel.getLineNum(), errorMsg));
			}
		}

		// 汇总报错信息返回给前端
		if (CollUtil.isNotEmpty(errorMessageList)) {
			return R.failed(errorMessageList);
		}
		return R.ok(null, "教师批量导入成功！");
	}

	@Override
	public R tempPassword(TempDTO dto){
		String password = ENCODER.encode(dto.getPassword());

		this.update(Wrappers.<SysUser>lambdaUpdate()
				.set(SysUser::getPassword, password)
				.eq(SysUser::getUsername, dto.getUsername()));
		return R.ok();
	}

}
