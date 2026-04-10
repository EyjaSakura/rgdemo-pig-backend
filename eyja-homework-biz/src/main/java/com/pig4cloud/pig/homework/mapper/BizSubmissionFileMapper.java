package com.pig4cloud.pig.homework.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.pig4cloud.pig.homework.entity.BizSubmissionFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* 提交附件表 Mapper
*
* @author EyjaSakura
* @since 2026-04-08 20:28
*/
@Mapper
public interface BizSubmissionFileMapper extends BaseMapper<BizSubmissionFile> {

    /**
     * 根据主键id查询
     *
     * @param fileId
     * @return 记录信息
     */
    BizSubmissionFile selectByPrimaryKey(Long fileId);

    /**
     * 根据主键删除数据
     *
     * @param fileId
     * @return 数量
     */
    int deleteByPrimaryKey(Long fileId);

    /**
     * 插入数据库记录（不建议使用）
     *
     * @param record
     */
    int insert(BizSubmissionFile record);

    /**
     * 插入数据库记录（建议使用）
     *
     * @param record 插入数据
     * @return 插入数量
     */
    int insertSelective(BizSubmissionFile record);

    /**
     * 修改数据(推荐使用)
     *
     * @param record 更新值
     * @return 更新数量
     */
    int updateByPrimaryKeySelective(BizSubmissionFile record);

    /**
     * 根据主键更新数据
     *
     * @param record 更新值
     * @return 更新数量
     */
    int updateByPrimaryKey(BizSubmissionFile record);
}
