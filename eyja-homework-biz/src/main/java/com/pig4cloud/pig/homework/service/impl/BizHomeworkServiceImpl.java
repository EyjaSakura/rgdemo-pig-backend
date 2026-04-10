package com.pig4cloud.pig.homework.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.homework.entity.*;
import com.pig4cloud.pig.homework.mapper.*;
import com.pig4cloud.pig.homework.service.BizHomeworkService;
import com.pig4cloud.pig.homework.vo.HomeworkDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 作业 Service 实现类
 *
 * @author EyjaSakura
 */
@Service
@RequiredArgsConstructor
public class BizHomeworkServiceImpl extends ServiceImpl<BizHomeworkMapper, BizHomework>
        implements BizHomeworkService {

    private final BizHomeworkAttachmentMapper attachmentMapper;
    private final BizEnrollmentMapper         enrollmentMapper;
    private final BizMessageMapper            messageMapper;
    private final BizTeachingMapper           teachingMapper;
    private final BizCourseMapper             courseMapper;

    @Override
    public HomeworkDetailVO getHomeworkDetail(Long homeworkId, Long studentId) {
        HomeworkDetailVO vo = baseMapper.selectHomeworkDetail(homeworkId, studentId);
        if (vo == null) return null;

        List<BizHomeworkAttachment> attachments = attachmentMapper.selectList(
                Wrappers.<BizHomeworkAttachment>lambdaQuery()
                        .eq(BizHomeworkAttachment::getHomeworkId, homeworkId));
        vo.setAttachments(attachments);

        return vo;
    }

    @Override
    public void pushHomeworkMessages(Long homeworkId) {
        BizHomework homework = getById(homeworkId);
        if (homework == null) return;

        BizTeaching teaching = teachingMapper.selectById(homework.getTeachingId());
        if (teaching == null) return;

        BizCourse course = courseMapper.selectById(teaching.getCourseCode());
        String courseName = course != null ? course.getCourseName() : "";

        List<BizEnrollment> enrollments = enrollmentMapper.selectList(
                Wrappers.<BizEnrollment>lambdaQuery()
                        .eq(BizEnrollment::getTeachingId, homework.getTeachingId())
                        .eq(BizEnrollment::getStatus, "0"));

        for (BizEnrollment enrollment : enrollments) {
            BizMessage msg = new BizMessage();
            msg.setReceiverId(enrollment.getStudentId());
            msg.setTitle("新作业：" + homework.getTitle());
            msg.setContent(courseName + " 发布了新作业，截止时间：" + homework.getDeadline());
            msg.setType("1");
            msg.setIsRead(0);
            messageMapper.insert(msg);
        }
    }
}
