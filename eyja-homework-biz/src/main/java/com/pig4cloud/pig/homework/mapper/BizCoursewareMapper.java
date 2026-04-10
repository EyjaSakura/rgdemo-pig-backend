package com.pig4cloud.pig.homework.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.pig4cloud.pig.homework.entity.BizCourseware;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* 课程资源/课件表 Mapper
*
* @author EyjaSakura
* @since 2026-04-08 20:28
*/
@Mapper
public interface BizCoursewareMapper extends BaseMapper<BizCourseware> {

    /**
     * 根据主键id查询
     *
     * @param coursewareId
     * @return 记录信息
     */
    BizCourseware selectByPrimaryKey(Long coursewareId);

    /**
     * 根据主键删除数据
     *
     * @param coursewareId
     * @return 数量
     */
    int deleteByPrimaryKey(Long coursewareId);

    /**
     * 插入数据库记录（不建议使用）
     *
     * @param record
     */
    int insert(BizCourseware record);

    /**
     * 插入数据库记录（建议使用）
     *
     * @param record 插入数据
     * @return 插入数量
     */
    int insertSelective(BizCourseware record);

    /**
     * 修改数据(推荐使用)
     *
     * @param record 更新值
     * @return 更新数量
     */
    int updateByPrimaryKeySelective(BizCourseware record);

    /**
     * 根据主键更新数据
     *
     * @param record 更新值
     * @return 更新数量
     */
    int updateByPrimaryKey(BizCourseware record);
}
