package com.pig4cloud.pig.homework.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.common.core.exception.CheckedException;
import com.pig4cloud.pig.homework.entity.*;
import com.pig4cloud.pig.homework.mapper.*;
import com.pig4cloud.pig.homework.service.BizHomeworkSubmissionService;
import com.pig4cloud.pig.homework.vo.SubmissionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 作业提交 Service 实现类
 *
 * @author EyjaSakura
 */
@Service
@RequiredArgsConstructor
public class BizHomeworkSubmissionServiceImpl
        extends ServiceImpl<BizHomeworkSubmissionMapper, BizHomeworkSubmission>
        implements BizHomeworkSubmissionService {

    private final BizSubmissionFileMapper submissionFileMapper;
    private final BizHomeworkMapper       homeworkMapper;
    private final BizMessageMapper        messageMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitHomework(BizHomeworkSubmission submission,
                               List<String> fileUrls, List<String> fileNames,
                               List<Long> fileSizes, List<String> fileTypes) {
        BizHomework homework = homeworkMapper.selectById(submission.getHomeworkId());
        if (homework == null) throw new CheckedException("作业不存在");

        boolean isLate = LocalDateTime.now().isAfter(homework.getDeadline());

        BizHomeworkSubmission existing = getOne(Wrappers.<BizHomeworkSubmission>lambdaQuery()
                .eq(BizHomeworkSubmission::getHomeworkId, submission.getHomeworkId())
                .eq(BizHomeworkSubmission::getStudentId, submission.getStudentId()));

        if (existing != null) {
            submissionFileMapper.delete(Wrappers.<BizSubmissionFile>lambdaQuery()
                    .eq(BizSubmissionFile::getSubmissionId, existing.getSubmissionId()));
            existing.setSubmitContent(submission.getSubmitContent());
            existing.setStatus(isLate ? "1" : "1");
            existing.setSubmitTime(LocalDateTime.now());
            existing.setScore(null);
            existing.setTeacherComment(null);
            existing.setGradeBy(null);
            existing.setGradeTime(null);
            updateById(existing);
            submission.setSubmissionId(existing.getSubmissionId());
        } else {
            submission.setStatus("1");
            submission.setSubmitTime(LocalDateTime.now());
            save(submission);
        }

        if (fileUrls != null) {
            for (int i = 0; i < fileUrls.size(); i++) {
                BizSubmissionFile file = new BizSubmissionFile();
                file.setSubmissionId(submission.getSubmissionId());
                file.setFileUrl(fileUrls.get(i));
                file.setFileName(fileNames != null && i < fileNames.size() ? fileNames.get(i) : "");
                file.setFileSize(fileSizes != null && i < fileSizes.size() ? fileSizes.get(i) : null);
                file.setFileType(fileTypes != null && i < fileTypes.size() ? fileTypes.get(i) : "other");
                file.setUploadTime(new Date());
                submissionFileMapper.insert(file);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void gradeSubmission(Long submissionId, BigDecimal score, String comment, Long gradedById) {
        BizHomeworkSubmission submission = getById(submissionId);
        if (submission == null) throw new CheckedException("提交记录不存在");

        submission.setScore(score);
        submission.setTeacherComment(comment);
        submission.setGradeBy(String.valueOf(gradedById));
        submission.setGradeTime(LocalDateTime.now());
        submission.setStatus("2");
        updateById(submission);

        BizMessage msg = new BizMessage();
        msg.setReceiverId(submission.getStudentId());
        msg.setTitle("您的作业已批阅");
        msg.setContent("您的作业已完成批阅，得分：" + score + "，请及时查看。");
        msg.setType("2");
        msg.setIsRead(0);
        messageMapper.insert(msg);
    }

    @Override
    public IPage<SubmissionVO> pageSubmissionsByHomework(Page<?> page, Long homeworkId, String status) {
        IPage<SubmissionVO> result = baseMapper.selectSubmissionsByHomework(page, homeworkId, status);
        result.getRecords().forEach(vo -> {
            if (vo.getSubmissionId() != null) {
                List<BizSubmissionFile> files = submissionFileMapper.selectList(
                        Wrappers.<BizSubmissionFile>lambdaQuery()
                                .eq(BizSubmissionFile::getSubmissionId, vo.getSubmissionId()));
                vo.setFiles(files);
            }
        });
        return result;
    }

    @Override
    public IPage<SubmissionVO> pageStudentSubmissions(Page<?> page, Long teachingId, Long studentId) {
        return baseMapper.selectStudentSubmissions(page, teachingId, studentId);
    }
}
