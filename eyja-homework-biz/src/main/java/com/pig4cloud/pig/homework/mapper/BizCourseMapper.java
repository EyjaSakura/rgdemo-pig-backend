package com.pig4cloud.pig.homework.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.pig4cloud.pig.homework.entity.BizCourse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* 课程信息表 Mapper
*
* @author EyjaSakura
* @since 2026-04-08 20:28
*/
@Mapper
public interface BizCourseMapper extends BaseMapper<BizCourse> {

    /**
     * 根据主键id查询
     *
     * @param courseCode
     * @return 记录信息
     */
    BizCourse selectByPrimaryKey(String courseCode);

    /**
     * 根据主键删除数据
     *
     * @param courseCode
     * @return 数量
     */
    int deleteByPrimaryKey(String courseCode);

    /**
     * 插入数据库记录（不建议使用）
     *
     * @param record
     */
    int insert(BizCourse record);

    /**
     * 插入数据库记录（建议使用）
     *
     * @param record 插入数据
     * @return 插入数量
     */
    int insertSelective(BizCourse record);

    /**
     * 修改数据(推荐使用)
     *
     * @param record 更新值
     * @return 更新数量
     */
    int updateByPrimaryKeySelective(BizCourse record);

    /**
     * 根据主键更新数据
     *
     * @param record 更新值
     * @return 更新数量
     */
    int updateByPrimaryKeyWithBLOBs(BizCourse record);

    /**
     * 根据主键更新数据
     *
     * @param record 更新值
     * @return 更新数量
     */
    int updateByPrimaryKey(BizCourse record);
}
