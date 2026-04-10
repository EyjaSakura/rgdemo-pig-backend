package com.pig4cloud.pig.homework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.common.core.exception.CheckedException;
import com.pig4cloud.pig.homework.entity.BizCourseware;
import com.pig4cloud.pig.homework.entity.BizTeaching;
import com.pig4cloud.pig.homework.mapper.BizCoursewareMapper;
import com.pig4cloud.pig.homework.mapper.BizTeachingMapper;
import com.pig4cloud.pig.homework.service.BizCoursewareService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 课件 Service 实现类
 *
 * @author EyjaSakura
 */
@Service
@RequiredArgsConstructor
public class BizCoursewareServiceImpl extends ServiceImpl<BizCoursewareMapper, BizCourseware>
		implements BizCoursewareService {

	private final BizTeachingMapper teachingMapper;

	@Override
	public Boolean addCourseware(BizCourseware courseware) {
		// 校验任课存在
		BizTeaching teaching = teachingMapper.selectById(courseware.getTeachingId());
		if (teaching == null || "1".equals(teaching.getDelFlag())) {
			throw new CheckedException("任课记录不存在");
		}
		courseware.setUploadTime(new Date());
		courseware.setDelFlag("0");
		// 默认排序权重
		if (courseware.getSortOrder() == null) {
			courseware.setSortOrder(0);
		}
		return this.save(courseware);
	}

	@Override
	public List<BizCourseware> listByTeaching(Long teachingId, String folderName) {
		LambdaQueryWrapper<BizCourseware> wrapper = Wrappers.<BizCourseware>lambdaQuery()
				.eq(BizCourseware::getTeachingId, teachingId)
				.eq(BizCourseware::getDelFlag, "0")
				.eq(folderName != null, BizCourseware::getFolderName, folderName)
				.orderByAsc(BizCourseware::getSortOrder)
				.orderByDesc(BizCourseware::getUploadTime);
		return this.list(wrapper);
	}

	@Override
	public Boolean removeCourseware(Long coursewareId) {
		BizCourseware existing = this.getById(coursewareId);
		if (existing == null || "1".equals(existing.getDelFlag())) {
			throw new CheckedException("课件不存在");
		}
		existing.setDelFlag("1");
		return this.updateById(existing);
	}
}
