package com.pig4cloud.pig.homework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.homework.entity.BizTeachingAssistant;

import java.util.List;

/**
 * 助教 Service
 */
public interface BizTeachingAssistantService extends IService<BizTeachingAssistant> {

	/**
	 * 查询某用户作为助教所被授权的 teaching_id 列表（数据隔离用）
	 */
	List<Long> getAuthorizedTeachingIds(Long assistantId);

	/**
	 * 判断某用户是否为某门课的助教
	 */
	boolean isAssistant(Long teachingId, Long userId);

	/**
	 * 指派助教
	 */
	Boolean assignAssistant(BizTeachingAssistant assistant);

	/**
	 * 查看某任课的助教列表
	 */
	List<BizTeachingAssistant> listByTeaching(Long teachingId);

	/**
	 * 移除助教（逻辑删除）
	 */
	Boolean removeAssistant(Long id);
}
