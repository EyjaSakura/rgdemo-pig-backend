package com.pig4cloud.pig.homework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.homework.entity.BizCourseware;

import java.util.List;

/**
 * 课件 Service
 */
public interface BizCoursewareService extends IService<BizCourseware> {

	/**
	 * 上传课件
	 */
	Boolean addCourseware(BizCourseware courseware);

	/**
	 * 获取某任课的课件列表（可按文件夹分组）
	 */
	List<BizCourseware> listByTeaching(Long teachingId, String folderName);

	/**
	 * 删除课件
	 */
	Boolean removeCourseware(Long coursewareId);
}
