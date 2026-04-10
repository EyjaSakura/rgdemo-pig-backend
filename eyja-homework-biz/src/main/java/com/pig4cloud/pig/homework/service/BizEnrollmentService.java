package com.pig4cloud.pig.homework.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.homework.entity.BizEnrollment;
import com.pig4cloud.pig.homework.vo.EnrollmentVO;

import java.util.List;

/**
 * 选课 Service
 */
public interface BizEnrollmentService extends IService<BizEnrollment> {

    /**
     * 学生选课
     * @param teachingId 任课ID
     * @param studentId  学生ID
     */
    void enroll(Long teachingId, Long studentId);

    /**
     * 学生退课
     */
    void drop(Long teachingId, Long studentId);

    /**
     * 查询某任课下的学生名单（分页）
     */
    IPage<EnrollmentVO> pageStudentsByTeaching(Page page, Long teachingId);

    /**
     * 查询某学生的选课列表
     */
    List<EnrollmentVO> listByStudent(Long studentId, Integer isActive);
}
