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
import com.pig4cloud.pig.homework.entity.HomeworkEntity;
import com.pig4cloud.pig.homework.service.HomeworkService;

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
 * 作业发布表
 *
 * @author EyjaSakura
 * @date 2026-03-22 16:06:40
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/homework" )
@Tag(description = "homework" , name = "作业发布表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class HomeworkController {

    private final  HomeworkService homeworkService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param homework 作业发布表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("homework_homework_view")
    public R getHomeworkPage(@ParameterObject Page page, @ParameterObject HomeworkEntity homework) {
        LambdaQueryWrapper<HomeworkEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(Objects.nonNull(homework.getCourseId()),HomeworkEntity::getCourseId,homework.getCourseId());
		wrapper.like(StrUtil.isNotBlank(homework.getTitle()),HomeworkEntity::getTitle,homework.getTitle());
        return R.ok(homeworkService.page(page, wrapper));
    }


    /**
     * 通过条件查询作业发布表
     * @param homework 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("homework_homework_view")
    public R getDetails(@ParameterObject HomeworkEntity homework) {
        return R.ok(homeworkService.list(Wrappers.query(homework)));
    }

    /**
     * 新增作业发布表
     * @param homework 作业发布表
     * @return R
     */
    @Operation(summary = "新增作业发布表" , description = "新增作业发布表" )
    @SysLog("新增作业发布表" )
    @PostMapping
    @HasPermission("homework_homework_add")
    public R save(@RequestBody HomeworkEntity homework) {
        return R.ok(homeworkService.save(homework));
    }

    /**
     * 修改作业发布表
     * @param homework 作业发布表
     * @return R
     */
    @Operation(summary = "修改作业发布表" , description = "修改作业发布表" )
    @SysLog("修改作业发布表" )
    @PutMapping
    @HasPermission("homework_homework_edit")
    public R updateById(@RequestBody HomeworkEntity homework) {
        return R.ok(homeworkService.updateById(homework));
    }

    /**
     * 通过id删除作业发布表
     * @param ids homeworkId列表
     * @return R
     */
    @Operation(summary = "通过id删除作业发布表" , description = "通过id删除作业发布表" )
    @SysLog("通过id删除作业发布表" )
    @DeleteMapping
    @HasPermission("homework_homework_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(homeworkService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param homework 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("homework_homework_export")
    public List<HomeworkEntity> exportExcel(HomeworkEntity homework,Long[] ids) {
        return homeworkService.list(Wrappers.lambdaQuery(homework).in(ArrayUtil.isNotEmpty(ids), HomeworkEntity::getHomeworkId, ids));
    }

    /**
     * 导入excel 表
     * @param homeworkList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("homework_homework_export")
    public R importExcel(@RequestExcel List<HomeworkEntity> homeworkList, BindingResult bindingResult) {
        return R.ok(homeworkService.saveBatch(homeworkList));
    }
}
