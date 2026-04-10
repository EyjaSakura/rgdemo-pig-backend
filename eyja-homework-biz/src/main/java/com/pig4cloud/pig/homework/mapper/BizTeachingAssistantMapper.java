package com.pig4cloud.pig.homework.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.pig4cloud.pig.homework.entity.BizTeachingAssistant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* 助教表 Mapper
*
* @author EyjaSakura
* @since 2026-04-08 20:28
*/
@Mapper
public interface BizTeachingAssistantMapper extends BaseMapper<BizTeachingAssistant> {

    /**
     * 根据主键id查询
     *
     * @param id
     * @return 记录信息
     */
    BizTeachingAssistant selectByPrimaryKey(Long id);

    /**
     * 根据主键删除数据
     *
     * @param id
     * @return 数量
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（不建议使用）
     *
     * @param record
     */
    int insert(BizTeachingAssistant record);

    /**
     * 插入数据库记录（建议使用）
     *
     * @param record 插入数据
     * @return 插入数量
     */
    int insertSelective(BizTeachingAssistant record);

    /**
     * 修改数据(推荐使用)
     *
     * @param record 更新值
     * @return 更新数量
     */
    int updateByPrimaryKeySelective(BizTeachingAssistant record);

    /**
     * 根据主键更新数据
     *
     * @param record 更新值
     * @return 更新数量
     */
    int updateByPrimaryKey(BizTeachingAssistant record);
}
