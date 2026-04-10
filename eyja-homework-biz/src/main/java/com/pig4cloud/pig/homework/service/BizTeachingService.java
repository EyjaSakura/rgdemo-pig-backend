package com.pig4cloud.pig.homework.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.homework.entity.BizTeaching;
import com.pig4cloud.pig.homework.vo.TeachingDetailVO;

import java.util.List;

/**
 * 任课 Service
 */
public interface BizTeachingService extends IService<BizTeaching> {

	/**
	 * 分页查询任课列表（联查）
	 */
	IPage<TeachingDetailVO> pageTeaching(Page page, Long collegeId, String courseCode,
										 Integer teachYear, Long teacherId, Integer status);

	/**
	 * 教师查看自己的任课列表
	 */
	List<TeachingDetailVO> listMyTeachings(Long teacherId, Integer teachYear, Integer status);

	/**
	 * 获取任课详情（联查课程名、教师名、助教）
	 */
	TeachingDetailVO getTeachingDetail(Long teachingId);

	/**
	 * 学生查看可选课列表（已排除已选和已满）
	 */
	List<TeachingDetailVO> listAvailableForStudent(Long studentId, Integer teachYear);

	/**
	 * 创建任课
	 */
	Boolean addTeaching(BizTeaching teaching);

	/**
	 * 修改任课
	 */
	Boolean updateTeaching(BizTeaching teaching);

	/**
	 * 删除任课（逻辑删除）
	 */
	Boolean removeTeaching(Long teachingId);
}
