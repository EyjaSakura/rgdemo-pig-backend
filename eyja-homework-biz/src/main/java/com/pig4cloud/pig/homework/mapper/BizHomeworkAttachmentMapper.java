package com.pig4cloud.pig.homework.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.pig4cloud.pig.homework.entity.BizHomeworkAttachment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* 作业附件表 Mapper
*
* @author EyjaSakura
* @since 2026-04-08 20:28
*/
@Mapper
public interface BizHomeworkAttachmentMapper extends BaseMapper<BizHomeworkAttachment> {

    /**
     * 根据主键id查询
     *
     * @param attachmentId
     * @return 记录信息
     */
    BizHomeworkAttachment selectByPrimaryKey(Long attachmentId);

    /**
     * 根据主键删除数据
     *
     * @param attachmentId
     * @return 数量
     */
    int deleteByPrimaryKey(Long attachmentId);

    /**
     * 插入数据库记录（不建议使用）
     *
     * @param record
     */
    int insert(BizHomeworkAttachment record);

    /**
     * 插入数据库记录（建议使用）
     *
     * @param record 插入数据
     * @return 插入数量
     */
    int insertSelective(BizHomeworkAttachment record);

    /**
     * 修改数据(推荐使用)
     *
     * @param record 更新值
     * @return 更新数量
     */
    int updateByPrimaryKeySelective(BizHomeworkAttachment record);

    /**
     * 根据主键更新数据
     *
     * @param record 更新值
     * @return 更新数量
     */
    int updateByPrimaryKey(BizHomeworkAttachment record);
}
