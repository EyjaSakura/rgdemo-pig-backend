package com.pig4cloud.pig.homework.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.pig4cloud.pig.homework.entity.*;
import com.pig4cloud.pig.homework.mapper.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/homework/stat")
@Tag(name = "统计仪表盘", description = "stat")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class BizStatController {

    private final BizHomeworkMapper homeworkMapper;
    private final BizTeachingMapper teachingMapper;
    private final BizCourseMapper courseMapper;
    private final BizCollegeMapper collegeMapper;
    private final BizEnrollmentMapper enrollmentMapper;
    private final BizHomeworkSubmissionMapper submissionMapper;

    @Operation(summary = "首页仪表盘统计数据")
    @GetMapping("/dashboard")
    public R dashboard() {
        Long userId = SecurityUtils.getUser().getId();
        Map<String, Object> data = new HashMap<>();

        // 统计课程数量（当前用户相关的教学任务数）
        long teachingCount = teachingMapper.selectCount(Wrappers.<BizTeaching>lambdaQuery()
                .eq(BizTeaching::getDelFlag, "0"));
        data.put("teachingCount", teachingCount);

        // 统计课程数量（不重复的课程号）
        long courseCount = courseMapper.selectCount(Wrappers.<BizCourse>lambdaQuery()
                .eq(BizCourse::getDelFlag, "0"));
        data.put("courseCount", courseCount);

        // 统计学院数量
        long collegeCount = collegeMapper.selectCount(Wrappers.<BizCollege>lambdaQuery()
                .eq(BizCollege::getDelFlag, "0"));
        data.put("collegeCount", collegeCount);

        // 统计选课总人次
        long enrollmentCount = enrollmentMapper.selectCount(Wrappers.<BizEnrollment>lambdaQuery()
                .eq(BizEnrollment::getDelFlag, "0")
                .eq(BizEnrollment::getStatus, "0"));
        data.put("enrollmentCount", enrollmentCount);

        // 教师视角：待批改作业数
        // 先查找该教师的所有教学任务
        long pendingGradeCount = 0;
        long publishedCount = 0;
        long upcomingCount = 0;
        long myTeachingCount = teachingMapper.selectCount(Wrappers.<BizTeaching>lambdaQuery()
                .eq(BizTeaching::getTeacherId, userId)
                .eq(BizTeaching::getDelFlag, "0"));
        data.put("myTeachingCount", myTeachingCount);

        if (myTeachingCount > 0) {
            // 获取教师的所有教学任务ID
            java.util.List<Long> teachingIds = teachingMapper.selectList(
                    Wrappers.<BizTeaching>lambdaQuery()
                            .select(BizTeaching::getTeachingId)
                            .eq(BizTeaching::getTeacherId, userId)
                            .eq(BizTeaching::getDelFlag, "0"))
                    .stream().map(BizTeaching::getTeachingId).toList();

            // 已发布作业数
            publishedCount = homeworkMapper.selectCount(Wrappers.<BizHomework>lambdaQuery()
                    .in(BizHomework::getTeachingId, teachingIds)
                    .eq(BizHomework::getDelFlag, "0")
                    .le(BizHomework::getPublishTime, LocalDateTime.now()));

            // 即将截止（3天内）的作业数
            upcomingCount = homeworkMapper.selectCount(Wrappers.<BizHomework>lambdaQuery()
                    .in(BizHomework::getTeachingId, teachingIds)
                    .eq(BizHomework::getDelFlag, "0")
                    .ge(BizHomework::getDeadline, LocalDateTime.now())
                    .le(BizHomework::getDeadline, LocalDateTime.now().plusDays(3)));
        }

        data.put("pendingGrade", pendingGradeCount);
        data.put("published", publishedCount);
        data.put("upcoming", upcomingCount);

        // 学生视角：我的选课数
        long myEnrollmentCount = enrollmentMapper.selectCount(Wrappers.<BizEnrollment>lambdaQuery()
                .eq(BizEnrollment::getStudentId, userId)
                .eq(BizEnrollment::getDelFlag, "0")
                .eq(BizEnrollment::getStatus, "0"));
        data.put("myEnrollmentCount", myEnrollmentCount);

        // 学生视角：待提交作业数
        long myPendingHomework = 0;
        if (myEnrollmentCount > 0) {
            java.util.List<Long> myTeachingIds = enrollmentMapper.selectList(
                    Wrappers.<BizEnrollment>lambdaQuery()
                            .select(BizEnrollment::getTeachingId)
                            .eq(BizEnrollment::getStudentId, userId)
                            .eq(BizEnrollment::getDelFlag, "0")
                            .eq(BizEnrollment::getStatus, "0"))
                    .stream().map(BizEnrollment::getTeachingId).toList();

            // 找出这些教学任务下已发布且截止时间未过的作业
            java.util.List<Long> publishedHomeworkIds = homeworkMapper.selectList(
                    Wrappers.<BizHomework>lambdaQuery()
                            .select(BizHomework::getHomeworkId)
                            .in(BizHomework::getTeachingId, myTeachingIds)
                            .eq(BizHomework::getDelFlag, "0")
                            .le(BizHomework::getPublishTime, LocalDateTime.now())
                            .ge(BizHomework::getDeadline, LocalDateTime.now()))
                    .stream().map(BizHomework::getHomeworkId).toList();

            if (!publishedHomeworkIds.isEmpty()) {
                // 减去已提交的
                long submittedCount = submissionMapper.selectCount(
                        Wrappers.<BizHomeworkSubmission>lambdaQuery()
                                .in(BizHomeworkSubmission::getHomeworkId, publishedHomeworkIds)
                                .eq(BizHomeworkSubmission::getStudentId, userId)
                                .eq(BizHomeworkSubmission::getDelFlag, "0"));
                myPendingHomework = publishedHomeworkIds.size() - submittedCount;
            }
        }
        data.put("myPendingHomework", Math.max(0, myPendingHomework));

        return R.ok(data);
    }
}
