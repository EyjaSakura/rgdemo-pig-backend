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
import com.pig4cloud.pig.homework.entity.CoursewareEntity;
import com.pig4cloud.pig.homework.service.CoursewareService;

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
 * 课件管理表
 *
 * @author EyjaSakura
 * @date 2026-03-22 13:43:35
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/courseware" )
@Tag(description = "courseware" , name = "课件管理表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class CoursewareController {

    private final  CoursewareService coursewareService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param courseware 课件管理表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("homework_courseware_view")
    public R getCoursewarePage(@ParameterObject Page page, @ParameterObject CoursewareEntity courseware) {
        LambdaQueryWrapper<CoursewareEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(Objects.nonNull(courseware.getCourseId()),CoursewareEntity::getCourseId,courseware.getCourseId());
		wrapper.eq(StrUtil.isNotBlank(courseware.getFolderName()),CoursewareEntity::getFolderName,courseware.getFolderName());
		wrapper.like(StrUtil.isNotBlank(courseware.getTitle()),CoursewareEntity::getTitle,courseware.getTitle());
        return R.ok(coursewareService.page(page, wrapper));
    }


    /**
     * 通过条件查询课件管理表
     * @param courseware 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("homework_courseware_view")
    public R getDetails(@ParameterObject CoursewareEntity courseware) {
        return R.ok(coursewareService.list(Wrappers.query(courseware)));
    }

    /**
     * 新增课件管理表
     * @param courseware 课件管理表
     * @return R
     */
    @Operation(summary = "新增课件管理表" , description = "新增课件管理表" )
    @SysLog("新增课件管理表" )
    @PostMapping
    @HasPermission("homework_courseware_add")
    public R save(@RequestBody CoursewareEntity courseware) {
        return R.ok(coursewareService.save(courseware));
    }

    /**
     * 修改课件管理表
     * @param courseware 课件管理表
     * @return R
     */
    @Operation(summary = "修改课件管理表" , description = "修改课件管理表" )
    @SysLog("修改课件管理表" )
    @PutMapping
    @HasPermission("homework_courseware_edit")
    public R updateById(@RequestBody CoursewareEntity courseware) {
        return R.ok(coursewareService.updateById(courseware));
    }

    /**
     * 通过id删除课件管理表
     * @param ids coursewareId列表
     * @return R
     */
    @Operation(summary = "通过id删除课件管理表" , description = "通过id删除课件管理表" )
    @SysLog("通过id删除课件管理表" )
    @DeleteMapping
    @HasPermission("homework_courseware_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(coursewareService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param courseware 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("homework_courseware_export")
    public List<CoursewareEntity> exportExcel(CoursewareEntity courseware,Long[] ids) {
        return coursewareService.list(Wrappers.lambdaQuery(courseware).in(ArrayUtil.isNotEmpty(ids), CoursewareEntity::getCoursewareId, ids));
    }

    /**
     * 导入excel 表
     * @param coursewareList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("homework_courseware_export")
    public R importExcel(@RequestExcel List<CoursewareEntity> coursewareList, BindingResult bindingResult) {
        return R.ok(coursewareService.saveBatch(coursewareList));
    }
}
