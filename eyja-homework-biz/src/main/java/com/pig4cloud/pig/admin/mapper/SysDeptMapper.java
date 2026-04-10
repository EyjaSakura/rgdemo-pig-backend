package com.pig4cloud.pig.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pig4cloud.pig.admin.api.entity.SysDept;
import org.apache.ibatis.annotations.Mapper;

/**
 * 部门 Mapper（在 eyja-homework-biz 中声明，直连同一 MySQL 库的 sys_dept 表，只读使用）
 * 避免依赖 pig-upms-biz 的编译产物。
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {

}
