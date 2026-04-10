package com.pig4cloud.pig.homework.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.pig4cloud.pig.homework.entity.BizEnrollment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* 选课表 Mapper
*
* @author EyjaSakura
* @since 2026-04-08 20:28
*/
@Mapper
public interface BizEnrollmentMapper extends BaseMapper<BizEnrollment> {

    /**
     * 根据主键id查询
     *
     * @param enrollmentId
     * @return 记录信息
     */
    BizEnrollment selectByPrimaryKey(Long enrollmentId);

    /**
     * 根据主键删除数据
     *
     * @param enrollmentId
     * @return 数量
     */
    int deleteByPrimaryKey(Long enrollmentId);

    /**
     * 插入数据库记录（不建议使用）
     *
     * @param record
     */
    int insert(BizEnrollment record);

    /**
     * 插入数据库记录（建议使用）
     *
     * @param record 插入数据
     * @return 插入数量
     */
    int insertSelective(BizEnrollment record);

    /**
     * 修改数据(推荐使用)
     *
     * @param record 更新值
     * @return 更新数量
     */
    int updateByPrimaryKeySelective(BizEnrollment record);

    /**
     * 根据主键更新数据
     *
     * @param record 更新值
     * @return 更新数量
     */
    int updateByPrimaryKey(BizEnrollment record);
}
