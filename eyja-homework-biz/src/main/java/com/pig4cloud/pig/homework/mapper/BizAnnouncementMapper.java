package com.pig4cloud.pig.homework.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.pig4cloud.pig.homework.entity.BizAnnouncement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* 通知公告表 Mapper
*
* @author EyjaSakura
* @since 2026-04-08 20:28
*/
@Mapper
public interface BizAnnouncementMapper extends BaseMapper<BizAnnouncement> {

    /**
     * 根据主键id查询
     *
     * @param announcementId
     * @return 记录信息
     */
    BizAnnouncement selectByPrimaryKey(Long announcementId);

    /**
     * 根据主键删除数据
     *
     * @param announcementId
     * @return 数量
     */
    int deleteByPrimaryKey(Long announcementId);

    /**
     * 插入数据库记录（不建议使用）
     *
     * @param record
     */
    int insert(BizAnnouncement record);

    /**
     * 插入数据库记录（建议使用）
     *
     * @param record 插入数据
     * @return 插入数量
     */
    int insertSelective(BizAnnouncement record);

    /**
     * 修改数据(推荐使用)
     *
     * @param record 更新值
     * @return 更新数量
     */
    int updateByPrimaryKeySelective(BizAnnouncement record);

    /**
     * 根据主键更新数据
     *
     * @param record 更新值
     * @return 更新数量
     */
    int updateByPrimaryKeyWithBLOBs(BizAnnouncement record);

    /**
     * 根据主键更新数据
     *
     * @param record 更新值
     * @return 更新数量
     */
    int updateByPrimaryKey(BizAnnouncement record);
}
