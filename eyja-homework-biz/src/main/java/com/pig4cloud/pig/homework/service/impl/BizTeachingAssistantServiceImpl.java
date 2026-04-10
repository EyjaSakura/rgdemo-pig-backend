package com.pig4cloud.pig.homework.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.common.core.exception.CheckedException;
import com.pig4cloud.pig.homework.entity.BizTeaching;
import com.pig4cloud.pig.homework.entity.BizTeachingAssistant;
import com.pig4cloud.pig.homework.mapper.BizTeachingAssistantMapper;
import com.pig4cloud.pig.homework.mapper.BizTeachingMapper;
import com.pig4cloud.pig.homework.service.BizTeachingAssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 助教 Service 实现类
 *
 * @author EyjaSakura
 */
@Service
@RequiredArgsConstructor
public class BizTeachingAssistantServiceImpl extends ServiceImpl<BizTeachingAssistantMapper, BizTeachingAssistant>
		implements BizTeachingAssistantService {

	private final BizTeachingMapper teachingMapper;

	@Override
	public List<Long> getAuthorizedTeachingIds(Long assistantId) {
		return list(Wrappers.<BizTeachingAssistant>lambdaQuery()
				.eq(BizTeachingAssistant::getAssistantId, assistantId)
				.eq(BizTeachingAssistant::getDelFlag, "0"))
				.stream().map(BizTeachingAssistant::getTeachingId)
				.collect(Collectors.toList());
	}

	@Override
	public boolean isAssistant(Long teachingId, Long userId) {
		return count(Wrappers.<BizTeachingAssistant>lambdaQuery()
				.eq(BizTeachingAssistant::getTeachingId, teachingId)
				.eq(BizTeachingAssistant::getAssistantId, userId)
				.eq(BizTeachingAssistant::getDelFlag, "0")) > 0;
	}

	@Override
	public Boolean assignAssistant(BizTeachingAssistant assistant) {
		// 校验任课存在
		BizTeaching teaching = teachingMapper.selectById(assistant.getTeachingId());
		if (teaching == null || "1".equals(teaching.getDelFlag())) {
			throw new CheckedException("任课记录不存在");
		}
		// 校验是否已指派同一助教
		long count = this.count(Wrappers.<BizTeachingAssistant>lambdaQuery()
				.eq(BizTeachingAssistant::getTeachingId, assistant.getTeachingId())
				.eq(BizTeachingAssistant::getAssistantId, assistant.getAssistantId())
				.eq(BizTeachingAssistant::getDelFlag, "0"));
		if (count > 0) {
			throw new CheckedException("该学生已经是这门课的助教");
		}
		assistant.setCreateTime(new Date());
		assistant.setDelFlag("0");
		return this.save(assistant);
	}

	@Override
	public List<BizTeachingAssistant> listByTeaching(Long teachingId) {
		return list(Wrappers.<BizTeachingAssistant>lambdaQuery()
				.eq(BizTeachingAssistant::getTeachingId, teachingId)
				.eq(BizTeachingAssistant::getDelFlag, "0")
				.orderByDesc(BizTeachingAssistant::getCreateTime));
	}

	@Override
	public Boolean removeAssistant(Long id) {
		BizTeachingAssistant existing = this.getById(id);
		if (existing == null || "1".equals(existing.getDelFlag())) {
			throw new CheckedException("助教指派记录不存在");
		}
		existing.setDelFlag("1");
		return this.updateById(existing);
	}
}
