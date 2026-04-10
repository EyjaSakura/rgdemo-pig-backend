package com.pig4cloud.pig.homework.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.homework.entity.BizAnnouncement;

/**
 * 公告 Service
 */
public interface BizAnnouncementService extends IService<BizAnnouncement> {

	/**
	 * 发布公告
	 */
	Boolean addAnnouncement(BizAnnouncement announcement);

	/**
	 * 分页查询公告（按层级过滤：系统级/学院级/课程级）
	 */
	IPage<BizAnnouncement> pageAnnouncement(Page page, Long collegeId, Long teachingId);

	/**
	 * 获取公告详情
	 */
	BizAnnouncement getDetail(Long announcementId);

	/**
	 * 修改公告
	 */
	Boolean updateAnnouncement(BizAnnouncement announcement);

	/**
	 * 删除公告（逻辑删除）
	 */
	Boolean removeAnnouncement(Long announcementId);
}
