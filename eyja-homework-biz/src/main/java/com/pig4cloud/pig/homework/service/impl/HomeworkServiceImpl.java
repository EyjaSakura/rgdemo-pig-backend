package com.pig4cloud.pig.homework.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.admin.api.feign.RemoteUserService;
import com.pig4cloud.pig.common.core.constant.SecurityConstants;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.service.PigUser;
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

import java.util.ArrayList;
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
	private final RemoteUserService remoteUserService;
	// private final CourseClassService courseClassService;

	@Override
	@Transactional(rollbackFor = Exception.class) // 保证主表和子表要么同时成功，要么同时回滚
	public Long publishHomework(HomeworkDTO homeworkDTO) {

		// 从 Token 中获取当前用户的角色集合
		boolean isTeacher = SecurityUtils.getRoles().contains("ROLE_TEACHER");
		if (!isTeacher) {
			throw new RuntimeException("越权操作：只有教师角色才能发布作业");
		}

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
		Long classDeptId = 2036279349052731394L;

		// 跨越微服务的远程调用
		// 传入班级 ID，并打上 SecurityConstants.FROM_IN 的暗号，证明是内部微服务发起的请求
		R<List<Long>> result = remoteUserService.getStudentIdsByDeptId(classDeptId, SecurityConstants.FROM_IN);

		// 如果远程调用成功，直接返回拿到的学生 ID 数组
		if (result != null && result.getCode() == 0) {
			return result.getData();
		}

		// 如果调用失败，返回空列表，防止作业发布报错
		return new ArrayList<>();
	}

}
