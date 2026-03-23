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
import com.pig4cloud.pig.homework.entity.CourseEntity;
import com.pig4cloud.pig.homework.service.CourseService;

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
 * 课程信息表
 *
 * @author EyjaSakura
 * @date 2026-03-22 13:01:47
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/course" )
@Tag(description = "course" , name = "课程信息表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class CourseController {

    private final  CourseService courseService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param course 课程信息表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("homework_course_view")
    public R getCoursePage(@ParameterObject Page page, @ParameterObject CourseEntity course) {
        LambdaQueryWrapper<CourseEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.like(StrUtil.isNotBlank(course.getCourseName()),CourseEntity::getCourseName,course.getCourseName());
		wrapper.eq(Objects.nonNull(course.getStatus()),CourseEntity::getStatus,course.getStatus());
        return R.ok(courseService.page(page, wrapper));
    }


    /**
     * 通过条件查询课程信息表
     * @param course 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("homework_course_view")
    public R getDetails(@ParameterObject CourseEntity course) {
        return R.ok(courseService.list(Wrappers.query(course)));
    }

    /**
     * 新增课程信息表
     * @param course 课程信息表
     * @return R
     */
    @Operation(summary = "新增课程信息表" , description = "新增课程信息表" )
    @SysLog("新增课程信息表" )
    @PostMapping
    @HasPermission("homework_course_add")
    public R save(@RequestBody CourseEntity course) {
        return R.ok(courseService.save(course));
    }

    /**
     * 修改课程信息表
     * @param course 课程信息表
     * @return R
     */
    @Operation(summary = "修改课程信息表" , description = "修改课程信息表" )
    @SysLog("修改课程信息表" )
    @PutMapping
    @HasPermission("homework_course_edit")
    public R updateById(@RequestBody CourseEntity course) {
        return R.ok(courseService.updateById(course));
    }

    /**
     * 通过id删除课程信息表
     * @param ids courseId列表
     * @return R
     */
    @Operation(summary = "通过id删除课程信息表" , description = "通过id删除课程信息表" )
    @SysLog("通过id删除课程信息表" )
    @DeleteMapping
    @HasPermission("homework_course_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(courseService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param course 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("homework_course_export")
    public List<CourseEntity> exportExcel(CourseEntity course,Long[] ids) {
        return courseService.list(Wrappers.lambdaQuery(course).in(ArrayUtil.isNotEmpty(ids), CourseEntity::getCourseId, ids));
    }

    /**
     * 导入excel 表
     * @param courseList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("homework_course_export")
    public R importExcel(@RequestExcel List<CourseEntity> courseList, BindingResult bindingResult) {
        return R.ok(courseService.saveBatch(courseList));
    }
}
