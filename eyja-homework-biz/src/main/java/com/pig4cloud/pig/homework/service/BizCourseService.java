package com.pig4cloud.pig.homework.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.homework.entity.BizCourse;

import java.util.List;
import java.util.Map;

/**
 * 课程 Service
 */
public interface BizCourseService extends IService<BizCourse> {

	/**
	 * 获取学院-课程树形结构
	 */
	List<Map<String, Object>> courseTree(String courseName);

	/**
	 * 获取课程列表（下拉框，可按学院过滤）
	 */
	List<BizCourse> listCourses(Long collegeId);

	/**
	 * 分页查询课程
	 */
	IPage<BizCourse> pageCourse(Page page, Long collegeId, String courseCode, String courseName);

	/**
	 * 获取课程详情
	 */
	BizCourse getCourseDetail(String courseCode);

	/**
	 * 新增课程
	 */
	Boolean addCourse(BizCourse course);

	/**
	 * 修改课程
	 */
	Boolean updateCourse(BizCourse course);

	/**
	 * 删除课程（逻辑删除）
	 */
	Boolean removeCourse(String courseCode);
}
