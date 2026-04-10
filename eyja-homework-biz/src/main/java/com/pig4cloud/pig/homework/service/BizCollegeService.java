package com.pig4cloud.pig.homework.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.homework.entity.BizCollege;

import java.util.List;
import java.util.Map;

/**
 * 学院 Service
 */
public interface BizCollegeService extends IService<BizCollege> {

	/**
	 * 获取全部学院列表（下拉框用）
	 */
	List<BizCollege> listAll();

	/**
	 * 分页查询学院
	 */
	IPage<BizCollege> pageCollege(Page page, String collegeName);

	/**
	 * 新增学院
	 */
	Boolean addCollege(BizCollege college);

	/**
	 * 修改学院
	 */
	Boolean updateCollege(BizCollege college);

	/**
	 * 删除学院（逻辑删除）
	 */
	Boolean removeCollege(Long collegeId);

	/**
	 * 从 sys_dept 同步学院数据到 biz_college
	 * @return 同步结果摘要 { total, added, updated, skipped }
	 */
	Map<String, Object> syncFromDept();
}
