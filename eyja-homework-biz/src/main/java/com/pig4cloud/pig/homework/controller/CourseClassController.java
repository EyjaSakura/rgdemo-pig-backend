package com.pig4cloud.pig.homework.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.pig4cloud.plugin.excel.annotation.RequestExcel;
import com.pig4cloud.pig.homework.entity.CourseClassEntity;
import com.pig4cloud.pig.homework.service.CourseClassService;

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
 * 必修课-班级自动关联表
 *
 * @author EyjaSakura
 * @date 2026-03-22 13:38:04
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/courseClass" )
@Tag(description = "courseClass" , name = "必修课-班级自动关联表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class CourseClassController {

    private final  CourseClassService courseClassService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param courseClass 必修课-班级自动关联表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("homework_courseClass_view")
    public R getCourseClassPage(@ParameterObject Page page, @ParameterObject CourseClassEntity courseClass) {
        LambdaQueryWrapper<CourseClassEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(Objects.nonNull(courseClass.getCourseId()),CourseClassEntity::getCourseId,courseClass.getCourseId());
		wrapper.eq(Objects.nonNull(courseClass.getDeptId()),CourseClassEntity::getDeptId,courseClass.getDeptId());
        return R.ok(courseClassService.page(page, wrapper));
    }


    /**
     * 通过条件查询必修课-班级自动关联表
     * @param courseClass 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("homework_courseClass_view")
    public R getDetails(@ParameterObject CourseClassEntity courseClass) {
        return R.ok(courseClassService.list(Wrappers.query(courseClass)));
    }

    /**
     * 新增必修课-班级自动关联表
     * @param courseClass 必修课-班级自动关联表
     * @return R
     */
    @Operation(summary = "新增必修课-班级自动关联表" , description = "新增必修课-班级自动关联表" )
    @PostMapping
    @HasPermission("homework_courseClass_add")
    public R save(@RequestBody CourseClassEntity courseClass) {
        return R.ok(courseClassService.save(courseClass));
    }

    /**
     * 修改必修课-班级自动关联表
     * @param courseClass 必修课-班级自动关联表
     * @return R
     */
    @Operation(summary = "修改必修课-班级自动关联表" , description = "修改必修课-班级自动关联表" )
    @PutMapping
    @HasPermission("homework_courseClass_edit")
    public R updateById(@RequestBody CourseClassEntity courseClass) {
        return R.ok(courseClassService.updateById(courseClass));
    }

    /**
     * 通过id删除必修课-班级自动关联表
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除必修课-班级自动关联表" , description = "通过id删除必修课-班级自动关联表" )
    @DeleteMapping
    @HasPermission("homework_courseClass_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(courseClassService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param courseClass 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("homework_courseClass_export")
    public List<CourseClassEntity> exportExcel(CourseClassEntity courseClass,Long[] ids) {
        return courseClassService.list(Wrappers.lambdaQuery(courseClass).in(ArrayUtil.isNotEmpty(ids), CourseClassEntity::getId, ids));
    }

    /**
     * 导入excel 表
     * @param courseClassList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("homework_courseClass_export")
    public R importExcel(@RequestExcel List<CourseClassEntity> courseClassList, BindingResult bindingResult) {
        return R.ok(courseClassService.saveBatch(courseClassList));
    }
}
