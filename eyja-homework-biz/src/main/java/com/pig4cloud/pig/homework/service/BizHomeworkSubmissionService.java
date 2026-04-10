package com.pig4cloud.pig.homework.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.homework.entity.BizHomeworkSubmission;
import com.pig4cloud.pig.homework.vo.SubmissionVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 作业提交 Service
 */
public interface BizHomeworkSubmissionService extends IService<BizHomeworkSubmission> {

    /**
     * 学生提交作业（含文件列表）
     * @param submission 提交实体
     * @param fileUrls   上传的文件 URL 列表（可为空）
     * @param fileNames  原始文件名列表
     */
    void submitHomework(BizHomeworkSubmission submission,
                        List<String> fileUrls, List<String> fileNames,
                        List<Long> fileSizes, List<String> fileTypes);

    /**
     * 批改作业
     * @param submissionId 提交ID
     * @param score        分数
     * @param comment      评语
     * @param gradedById   批改人ID（教师或助教）
     */
    void gradeSubmission(Long submissionId, BigDecimal score, String comment, Long gradedById);

    /**
     * 教师/助教查看某作业的所有提交（分页）
     */
    IPage<SubmissionVO> pageSubmissionsByHomework(Page<?> page, Long homeworkId, String status);

    /**
     * 学生查看自己在某门课下的作业列表（含提交状态）
     */
    IPage<SubmissionVO> pageStudentSubmissions(Page<?> page, Long teachingId, Long studentId);
}
