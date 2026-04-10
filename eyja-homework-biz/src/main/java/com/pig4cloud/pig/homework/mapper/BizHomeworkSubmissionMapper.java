package com.pig4cloud.pig.homework.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pig4cloud.pig.homework.entity.BizHomeworkSubmission;
import com.pig4cloud.pig.homework.vo.SubmissionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BizHomeworkSubmissionMapper extends BaseMapper<BizHomeworkSubmission> {

    /**
     * 分页查询某作业的所有提交（含学生姓名）
     */
    IPage<SubmissionVO> selectSubmissionsByHomework(IPage<?> page,
                                                    @Param("homeworkId") Long homeworkId,
                                                    @Param("status") String status);

    /**
     * 查询学生在某门课下的所有作业及提交状态
     */
    IPage<SubmissionVO> selectStudentSubmissions(IPage<?> page,
                                                  @Param("teachingId") Long teachingId,
                                                  @Param("studentId") Long studentId);
}
