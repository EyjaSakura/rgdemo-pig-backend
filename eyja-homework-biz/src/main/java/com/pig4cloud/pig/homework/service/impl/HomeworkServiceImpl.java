package com.pig4cloud.pig.homework.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.admin.api.entity.SysUser;
import com.pig4cloud.pig.admin.service.SysUserService;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.pig4cloud.pig.homework.dto.HomeworkDTO;
import com.pig4cloud.pig.homework.entity.HomeworkEntity;
import com.pig4cloud.pig.homework.entity.HomeworkSubmissionEntity;
import com.pig4cloud.pig.homework.mapper.HomeworkMapper;
import com.pig4cloud.pig.homework.service.HomeworkService;
import com.pig4cloud.pig.homework.service.HomeworkSubmissionService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 作业发布表
 *
 * @author EyjaSakura
 * @date 2026-03-22 16:06:40
 */
@Service
@AllArgsConstructor
public class HomeworkServiceImpl extends ServiceImpl<HomeworkMapper, HomeworkEntity> implements HomeworkService {

	// 注入作业提交子表的服务，用来给学生批量插入空记录
	private final HomeworkSubmissionService submissionService;

	// 关联表的代码还未完成
	// private final SysUserService sysUserService;
	// private final CourseClassService courseClassService;

	@Override
	@Transactional(rollbackFor = Exception.class) // 保证主表和子表要么同时成功，要么同时回滚
	public Long publishHomework(HomeworkDTO homeworkDTO) {

		// 将前端传来的 DTO 转换为实体类
		HomeworkEntity homeworkEntity = new HomeworkEntity();
		BeanUtils.copyProperties(homeworkDTO, homeworkEntity);

		// 获取当前登录的教师账号
		homeworkEntity.setCreateBy(SecurityUtils.getUser().getUsername());

		this.save(homeworkEntity);

		// 找出需要做这份作业的所有学生 ID
		List<Long> studentIds = getStudentIdsByCourseId(homeworkEntity.getCourseId());

		// 批量生成未交状态的提交记录
		if (CollUtil.isNotEmpty(studentIds)) {
			List<HomeworkSubmissionEntity> submissionList = studentIds.stream().map(studentId -> {
				HomeworkSubmissionEntity submission = new HomeworkSubmissionEntity();
				submission.setHomeworkId(homeworkEntity.getHomeworkId()); // 拿到刚才入库自动生成的作业ID
				submission.setStudentId(studentId);
				submission.setStatus("0"); // 0-未交
				return submission;
			}).collect(Collectors.toList());

			// 调用子表服务进行批量插入
			submissionService.saveBatch(submissionList);
		}

		return homeworkEntity.getHomeworkId();
	}

	// 根据课程 ID 获取所有需要交作业的学生 ID
	// 才发现用户故事连选课的操作都没有，好像学生现在只能有自己班级关联的必修课！！！
	private List<Long> getStudentIdsByCourseId(Long courseId) {
		// Long classDeptId = courseClassService.getDeptIdByCourseId(courseId);
		// 临时写死测试用，关联表的代码还未完成，这个是26届软工1班
//		Long classDeptId = 2036279349052731394L;
//
//		// 直接用 Pig 原生用户表查出这个班里所有的学生
//		List<SysUser> studentList = sysUserService.list(
//				com.baomidou.mybatisplus.core.toolkit.Wrappers.<SysUser>lambdaQuery()
//						.eq(SysUser::getDeptId, classDeptId)
//						.eq(SysUser::getUserType, "3") // 3 是学生
//		);
//
//		// 把查出来的 SysUser 对象列表，提取出他们的 ID 列表返回
//		return studentList.stream()
//				.map(SysUser::getUserId)
//				.collect(Collectors.toList());

		// 先硬编码
		return List.of(2036316085594120194L,2036316085342461954L,2036316085090803713L);
	}

}
