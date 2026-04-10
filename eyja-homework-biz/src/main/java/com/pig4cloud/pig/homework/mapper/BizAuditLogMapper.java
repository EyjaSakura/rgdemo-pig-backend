package com.pig4cloud.pig.homework.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.pig4cloud.pig.homework.entity.BizAuditLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* 操作审计日志表 Mapper
*
* @author EyjaSakura
* @since 2026-04-08 20:28
*/
@Mapper
public interface BizAuditLogMapper extends BaseMapper<BizAuditLog> {

    /**
     * 根据主键id查询
     *
     * @param logId
     * @return 记录信息
     */
    BizAuditLog selectByPrimaryKey(Long logId);

    /**
     * 根据主键删除数据
     *
     * @param logId
     * @return 数量
     */
    int deleteByPrimaryKey(Long logId);

    /**
     * 插入数据库记录（不建议使用）
     *
     * @param record
     */
    int insert(BizAuditLog record);

    /**
     * 插入数据库记录（建议使用）
     *
     * @param record 插入数据
     * @return 插入数量
     */
    int insertSelective(BizAuditLog record);

    /**
     * 修改数据(推荐使用)
     *
     * @param record 更新值
     * @return 更新数量
     */
    int updateByPrimaryKeySelective(BizAuditLog record);

    /**
     * 根据主键更新数据
     *
     * @param record 更新值
     * @return 更新数量
     */
    int updateByPrimaryKeyWithBLOBs(BizAuditLog record);

    /**
     * 根据主键更新数据
     *
     * @param record 更新值
     * @return 更新数量
     */
    int updateByPrimaryKey(BizAuditLog record);
}
