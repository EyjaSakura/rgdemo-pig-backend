package com.pig4cloud.pig.homework.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.homework.entity.BizHomework;
import com.pig4cloud.pig.homework.vo.HomeworkDetailVO;

/**
 * 作业 Service
 */
public interface BizHomeworkService extends IService<BizHomework> {

    /**
     * 查询作业详情（含附件）
     * @param homeworkId 作业ID
     * @param studentId  学生ID（传null则返回教师视角统计数据）
     */
    HomeworkDetailVO getHomeworkDetail(Long homeworkId, Long studentId);

    /**
     * 发布作业后，批量推送站内消息给该课所有学生
     */
    void pushHomeworkMessages(Long homeworkId);
}
