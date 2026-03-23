package com.pig4cloud.pig.homework.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.pig4cloud.plugin.excel.annotation.RequestExcel;
import com.pig4cloud.pig.homework.entity.ClassCommitteeEntity;
import com.pig4cloud.pig.homework.service.ClassCommitteeService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 班委职务分配表
 *
 * @author EyjaSakura
 * @date 2026-03-22 12:34:36
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/classCommittee" )
@Tag(description = "classCommittee" , name = "班委职务分配表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class ClassCommitteeController {

    private final  ClassCommitteeService classCommitteeService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param classCommittee 班委职务分配表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("homework_classCommittee_view")
    public R getClassCommitteePage(@ParameterObject Page page, @ParameterObject ClassCommitteeEntity classCommittee) {
        LambdaQueryWrapper<ClassCommitteeEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(Objects.nonNull(classCommittee.getDeptId()),ClassCommitteeEntity::getDeptId,classCommittee.getDeptId());
		wrapper.eq(StrUtil.isNotBlank(classCommittee.getRoleName()),ClassCommitteeEntity::getRoleName,classCommittee.getRoleName());
        return R.ok(classCommitteeService.page(page, wrapper));
    }


    /**
     * 通过条件查询班委职务分配表
     * @param classCommittee 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("homework_classCommittee_view")
    public R getDetails(@ParameterObject ClassCommitteeEntity classCommittee) {
        return R.ok(classCommitteeService.list(Wrappers.query(classCommittee)));
    }

    /**
     * 新增班委职务分配表
     * @param classCommittee 班委职务分配表
     * @return R
     */
    @Operation(summary = "新增班委职务分配表" , description = "新增班委职务分配表" )
    @SysLog("新增班委职务分配表" )
    @PostMapping
    @HasPermission("homework_classCommittee_add")
    public R save(@RequestBody ClassCommitteeEntity classCommittee) {
        return R.ok(classCommitteeService.save(classCommittee));
    }

    /**
     * 修改班委职务分配表
     * @param classCommittee 班委职务分配表
     * @return R
     */
    @Operation(summary = "修改班委职务分配表" , description = "修改班委职务分配表" )
    @SysLog("修改班委职务分配表" )
    @PutMapping
    @HasPermission("homework_classCommittee_edit")
    public R updateById(@RequestBody ClassCommitteeEntity classCommittee) {
        return R.ok(classCommitteeService.updateById(classCommittee));
    }

    /**
     * 通过id删除班委职务分配表
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除班委职务分配表" , description = "通过id删除班委职务分配表" )
    @SysLog("通过id删除班委职务分配表" )
    @DeleteMapping
    @HasPermission("homework_classCommittee_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(classCommitteeService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param classCommittee 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("homework_classCommittee_export")
    public List<ClassCommitteeEntity> exportExcel(ClassCommitteeEntity classCommittee,Long[] ids) {
        return classCommitteeService.list(Wrappers.lambdaQuery(classCommittee).in(ArrayUtil.isNotEmpty(ids), ClassCommitteeEntity::getId, ids));
    }

    /**
     * 导入excel 表
     * @param classCommitteeList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("homework_classCommittee_export")
    public R importExcel(@RequestExcel List<ClassCommitteeEntity> classCommitteeList, BindingResult bindingResult) {
        return R.ok(classCommitteeService.saveBatch(classCommitteeList));
    }
}
