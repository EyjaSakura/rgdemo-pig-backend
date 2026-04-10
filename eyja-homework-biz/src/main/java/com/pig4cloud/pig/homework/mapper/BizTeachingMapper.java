package com.pig4cloud.pig.homework.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.pig4cloud.pig.homework.entity.BizTeaching;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* 任课表（核心表） Mapper
*
* @author EyjaSakura
* @since 2026-04-08 20:28
*/
@Mapper
public interface BizTeachingMapper extends BaseMapper<BizTeaching> {

    /**
     * 根据主键id查询
     *
     * @param teachingId
     * @return 记录信息
     */
    BizTeaching selectByPrimaryKey(Long teachingId);

    /**
     * 根据主键删除数据
     *
     * @param teachingId
     * @return 数量
     */
    int deleteByPrimaryKey(Long teachingId);

    /**
     * 插入数据库记录（不建议使用）
     *
     * @param record
     */
    int insert(BizTeaching record);

    /**
     * 插入数据库记录（建议使用）
     *
     * @param record 插入数据
     * @return 插入数量
     */
    int insertSelective(BizTeaching record);

    /**
     * 修改数据(推荐使用)
     *
     * @param record 更新值
     * @return 更新数量
     */
    int updateByPrimaryKeySelective(BizTeaching record);

    /**
     * 根据主键更新数据
     *
     * @param record 更新值
     * @return 更新数量
     */
    int updateByPrimaryKey(BizTeaching record);
}
