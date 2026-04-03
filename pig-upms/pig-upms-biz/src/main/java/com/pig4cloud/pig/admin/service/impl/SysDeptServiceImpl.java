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
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.admin.api.entity.SysDept;
import com.pig4cloud.pig.admin.mapper.SysDeptMapper;
import com.pig4cloud.pig.admin.service.SysDeptService;
import com.pig4cloud.pig.common.core.constant.CacheConstants;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.plugin.excel.vo.ErrorMessage;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.cache.annotation.Cacheable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 部门管理服务实现类
 *
 * @author lengleng
 * @date 2025/05/30
 * @since 2018-01-20
 */
@Service
@AllArgsConstructor
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

	private final SysDeptMapper deptMapper;

	/**
	 * 根据部门ID删除部门（包含级联删除子部门）
	 * @param id 要删除的部门ID
	 * @return 删除操作是否成功，始终返回true
	 * @throws Exception 事务执行过程中可能抛出的异常
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean removeDeptById(Long id) {
		// 级联删除部门
		List<Long> idList = this.listDescendants(id).stream().map(SysDept::getDeptId).toList();

		Optional.ofNullable(idList).filter(CollUtil::isNotEmpty).ifPresent(this::removeByIds);

		return Boolean.TRUE;
	}

	/**
	 * 查询部门树结构
	 * @param deptName 部门名称(模糊查询)
	 * @return 部门树结构列表，模糊查询时返回平铺列表
	 */
	@Override
	public List<Tree<Long>> getDeptTree(String deptName) {
		// 查询全部部门
		List<SysDept> deptAllList = deptMapper
			.selectList(Wrappers.<SysDept>lambdaQuery().like(StrUtil.isNotBlank(deptName), SysDept::getName, deptName));

		// 权限内部门
		List<TreeNode<Long>> collect = deptAllList.stream()
			.filter(dept -> dept.getDeptId().intValue() != dept.getParentId())
			.sorted(Comparator.comparingInt(SysDept::getSortOrder))
			.map(dept -> {
				TreeNode<Long> treeNode = new TreeNode();
				treeNode.setId(dept.getDeptId());
				treeNode.setParentId(dept.getParentId());
				treeNode.setName(dept.getName());
				treeNode.setWeight(dept.getSortOrder());
				// 有权限不返回标识
				Map<String, Object> extra = new HashMap<>(8);
				extra.put(SysDept.Fields.createTime, dept.getCreateTime());
				treeNode.setExtra(extra);
				return treeNode;
			})
			.toList();

		// 模糊查询 不组装树结构 直接返回 表格方便编辑
		if (StrUtil.isNotBlank(deptName)) {
			return collect.stream().map(node -> {
				Tree<Long> tree = new Tree<>();
				tree.putAll(node.getExtra());
				BeanUtils.copyProperties(node, tree);
				return tree;
			}).toList();
		}

		return TreeUtil.build(collect, 0L);
	}

	/**
	 * 查询部门及其所有子部门
	 * @param deptId 目标部门ID
	 * @return 包含目标部门及其所有子部门的列表
	 */
	@Override
	public List<SysDept> listDescendants(Long deptId) {
		// 查询全部部门
		List<SysDept> allDeptList = baseMapper.selectList(Wrappers.emptyWrapper());

		// 递归查询所有子节点
		List<SysDept> resDeptList = new ArrayList<>();
		recursiveDept(allDeptList, deptId, resDeptList);

		// 添加当前节点
		resDeptList.addAll(allDeptList.stream().filter(sysDept -> deptId.equals(sysDept.getDeptId())).toList());
		return resDeptList;
	}

	/**
	 * 递归查询所有子节点
	 * @param allDeptList 所有部门列表
	 * @param parentId 父部门ID
	 * @param resDeptList 结果集合
	 */
	private void recursiveDept(List<SysDept> allDeptList, Long parentId, List<SysDept> resDeptList) {
		// 使用 Stream API 进行筛选和遍历
		allDeptList.stream().filter(sysDept -> sysDept.getParentId().equals(parentId)).forEach(sysDept -> {
			resDeptList.add(sysDept);
			recursiveDept(allDeptList, sysDept.getDeptId(), resDeptList);
		});
	}

	/**
	 * 获取全量部门的 Map (利用 Spring Cache 优雅实现 Redis 缓存)
	 * @return Map<deptId, SysDept>
	 */
	@Cacheable(value = "sys_dept", key = "'all_dept_map'")
	public Map<Long, SysDept> getAllDeptMapFromCache() {
		// 只有当 Redis 里没有数据时，才会执行到这里查数据库
		List<SysDept> allDepts = this.list(Wrappers.emptyWrapper());
		// 转换成 Map 结构，极速寻址
		return allDepts.stream()
				.collect(Collectors.toMap(SysDept::getDeptId, dept -> dept));
	}

	/**
	 * 获取指定部门的完整层级路径名称 (如: "学校-学院-专业-班级")
	 * @param targetDeptId 目标节点的 ID (比如班级 ID)
	 * @return 拼接好的完整路径字符串
	 */
	@Override
	public String getFullDeptPathName(Long targetDeptId) {
		if (targetDeptId == null) {
			return "";
		}

		// 1. 极速获取全局缓存 Map (O(1) 复杂度)
		Map<Long, SysDept> deptMap = this.getAllDeptMapFromCache();

		List<String> pathNames = new ArrayList<>();
		Long currentId = targetDeptId;

		// 2. 纯内存自底向上溯源
		while (currentId != null && currentId != 0L) {
			SysDept currentDept = deptMap.get(currentId);

			if (currentDept == null) {
				break; // 防御性编程：防止脏数据导致死循环
			}

			pathNames.add(currentDept.getName());
			currentId = currentDept.getParentId();
		}

		// 3. 结果是倒序的（班级->专业->学院），需要反转
		Collections.reverse(pathNames);

		// 4. 拼接返回
		return String.join("-", pathNames);
	}

}
