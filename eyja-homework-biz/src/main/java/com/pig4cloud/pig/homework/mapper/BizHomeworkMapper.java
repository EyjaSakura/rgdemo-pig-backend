package com.pig4cloud.pig.homework.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pig4cloud.pig.homework.entity.BizHomework;
import com.pig4cloud.pig.homework.vo.HomeworkDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BizHomeworkMapper extends BaseMapper<BizHomework> {

    /**
     * 查询作业详情（含附件、提交统计）
     */
    HomeworkDetailVO selectHomeworkDetail(@Param("homeworkId") Long homeworkId,
                                          @Param("studentId") Long studentId);
}
