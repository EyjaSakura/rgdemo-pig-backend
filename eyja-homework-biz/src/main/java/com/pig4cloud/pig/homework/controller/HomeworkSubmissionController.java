package com.pig4cloud.pig.homework.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.pig4cloud.plugin.excel.annotation.RequestExcel;
import com.pig4cloud.pig.homework.entity.HomeworkSubmissionEntity;
import com.pig4cloud.pig.homework.service.HomeworkSubmissionService;

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
 * 作业提交与批阅表
 *
 * @author EyjaSakura
 * @date 2026-03-22 16:11:52
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/homeworkSubmission" )
@Tag(description = "homeworkSubmission" , name = "作业提交与批阅表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class HomeworkSubmissionController {

    private final  HomeworkSubmissionService homeworkSubmissionService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param homeworkSubmission 作业提交与批阅表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("homework_homeworkSubmission_view")
    public R getHomeworkSubmissionPage(@ParameterObject Page page, @ParameterObject HomeworkSubmissionEntity homeworkSubmission) {
        LambdaQueryWrapper<HomeworkSubmissionEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(Objects.nonNull(homeworkSubmission.getHomeworkId()),HomeworkSubmissionEntity::getHomeworkId,homeworkSubmission.getHomeworkId());
		wrapper.eq(Objects.nonNull(homeworkSubmission.getStudentId()),HomeworkSubmissionEntity::getStudentId,homeworkSubmission.getStudentId());
		wrapper.eq(Objects.nonNull(homeworkSubmission.getStatus()),HomeworkSubmissionEntity::getStatus,homeworkSubmission.getStatus());
        return R.ok(homeworkSubmissionService.page(page, wrapper));
    }


    /**
     * 通过条件查询作业提交与批阅表
     * @param homeworkSubmission 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("homework_homeworkSubmission_view")
    public R getDetails(@ParameterObject HomeworkSubmissionEntity homeworkSubmission) {
        return R.ok(homeworkSubmissionService.list(Wrappers.query(homeworkSubmission)));
    }

    /**
     * 新增作业提交与批阅表
     * @param homeworkSubmission 作业提交与批阅表
     * @return R
     */
    @Operation(summary = "新增作业提交与批阅表" , description = "新增作业提交与批阅表" )
    @PostMapping
    @HasPermission("homework_homeworkSubmission_add")
    public R save(@RequestBody HomeworkSubmissionEntity homeworkSubmission) {
        return R.ok(homeworkSubmissionService.save(homeworkSubmission));
    }

    /**
     * 修改作业提交与批阅表
     * @param homeworkSubmission 作业提交与批阅表
     * @return R
     */
    @Operation(summary = "修改作业提交与批阅表" , description = "修改作业提交与批阅表" )
    @PutMapping
    @HasPermission("homework_homeworkSubmission_edit")
    public R updateById(@RequestBody HomeworkSubmissionEntity homeworkSubmission) {
        return R.ok(homeworkSubmissionService.updateById(homeworkSubmission));
    }

    /**
     * 通过id删除作业提交与批阅表
     * @param ids submissionId列表
     * @return R
     */
    @Operation(summary = "通过id删除作业提交与批阅表" , description = "通过id删除作业提交与批阅表" )
    @DeleteMapping
    @HasPermission("homework_homeworkSubmission_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(homeworkSubmissionService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param homeworkSubmission 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("homework_homeworkSubmission_export")
    public List<HomeworkSubmissionEntity> exportExcel(HomeworkSubmissionEntity homeworkSubmission,Long[] ids) {
        return homeworkSubmissionService.list(Wrappers.lambdaQuery(homeworkSubmission).in(ArrayUtil.isNotEmpty(ids), HomeworkSubmissionEntity::getSubmissionId, ids));
    }

    /**
     * 导入excel 表
     * @param homeworkSubmissionList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("homework_homeworkSubmission_export")
    public R importExcel(@RequestExcel List<HomeworkSubmissionEntity> homeworkSubmissionList, BindingResult bindingResult) {
        return R.ok(homeworkSubmissionService.saveBatch(homeworkSubmissionList));
    }
}
