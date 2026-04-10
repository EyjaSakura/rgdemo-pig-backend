package com.pig4cloud.pig.homework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.common.core.exception.CheckedException;
import com.pig4cloud.pig.homework.entity.BizAnnouncement;
import com.pig4cloud.pig.homework.entity.BizEnrollment;
import com.pig4cloud.pig.homework.entity.BizMessage;
import com.pig4cloud.pig.homework.entity.BizTeaching;
import com.pig4cloud.pig.homework.mapper.BizAnnouncementMapper;
import com.pig4cloud.pig.homework.mapper.BizEnrollmentMapper;
import com.pig4cloud.pig.homework.mapper.BizTeachingMapper;
import com.pig4cloud.pig.homework.service.BizAnnouncementService;
import com.pig4cloud.pig.homework.service.BizMessageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 公告 Service 实现类
 *
 * @author EyjaSakura
 */
@Service
@RequiredArgsConstructor
public class BizAnnouncementServiceImpl extends ServiceImpl<BizAnnouncementMapper, BizAnnouncement>
		implements BizAnnouncementService {

	private final BizTeachingMapper teachingMapper;
	private final BizEnrollmentMapper enrollmentMapper;
	private final BizMessageService messageService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean addAnnouncement(BizAnnouncement announcement) {
		// 如果是课程级公告，校验任课存在
		if (announcement.getTeachingId() != null) {
			BizTeaching teaching = teachingMapper.selectById(announcement.getTeachingId());
			if (teaching == null || "1".equals(teaching.getDelFlag())) {
				throw new CheckedException("任课记录不存在");
			}
		}
		announcement.setCreateTime(new Date());
		announcement.setUpdateTime(new Date());
		announcement.setDelFlag("0");
		boolean saved = this.save(announcement);

		// 发布成功后，推送消息到消息中心
		if (saved) {
			sendAnnouncementNotification(announcement);
		}
		return saved;
	}

	/**
	 * 发布公告后推送消息通知
	 */
	private void sendAnnouncementNotification(BizAnnouncement announcement) {
		List<Long> receiverIds = new ArrayList<>();

		if (announcement.getTeachingId() != null) {
			// 课程级公告：推送给该课程的所有选课学生
			List<BizEnrollment> enrollments = enrollmentMapper.selectList(
				new LambdaQueryWrapper<BizEnrollment>()
					.eq(BizEnrollment::getTeachingId, announcement.getTeachingId())
					.eq(BizEnrollment::getStatus, "0")
			);
			for (BizEnrollment e : enrollments) {
				receiverIds.add(e.getStudentId());
			}
		} else if (announcement.getCollegeId() != null) {
			// 学院级公告：推送给该学院下的所有选课学生
			List<BizTeaching> teachings = teachingMapper.selectList(
				new LambdaQueryWrapper<BizTeaching>()
					.eq(BizTeaching::getCollegeId, announcement.getCollegeId())
					.eq(BizTeaching::getDelFlag, "0")
			);
			for (BizTeaching t : teachings) {
				List<BizEnrollment> enrollments = enrollmentMapper.selectList(
					new LambdaQueryWrapper<BizEnrollment>()
						.eq(BizEnrollment::getTeachingId, t.getTeachingId())
						.eq(BizEnrollment::getStatus, "0")
				);
				for (BizEnrollment e : enrollments) {
					if (!receiverIds.contains(e.getStudentId())) {
						receiverIds.add(e.getStudentId());
					}
				}
			}
		} else {
			// 系统级公告：推送给所有学生（角色 student 的用户）
			// 通过 biz_enrollment 去重获取所有学生ID
			List<BizEnrollment> allEnrollments = enrollmentMapper.selectList(
				new LambdaQueryWrapper<BizEnrollment>()
					.eq(BizEnrollment::getStatus, "0")
					.select(BizEnrollment::getStudentId)
					.groupBy(BizEnrollment::getStudentId)
			);
			for (BizEnrollment e : allEnrollments) {
				receiverIds.add(e.getStudentId());
			}
		}

		// 去重并批量写入消息
		List<Long> distinctIds = receiverIds.stream().distinct().toList();
		List<BizMessage> messages = new ArrayList<>();
		Date now = new Date();
		for (Long receiverId : distinctIds) {
			messages.add(BizMessage.builder()
				.receiverId(receiverId)
				.title("【公告】" + announcement.getTitle())
				.content(announcement.getContent() != null ? announcement.getContent() : "")
				.type("3") // 3 = 课程通知
				.isRead(0)
				.createTime(now)
				.delFlag("0")
				.build());
		}
		if (!messages.isEmpty()) {
			messageService.saveBatch(messages);
		}
	}

	@Override
	public IPage<BizAnnouncement> pageAnnouncement(Page page, Long collegeId, Long teachingId) {
		LambdaQueryWrapper<BizAnnouncement> wrapper = Wrappers.<BizAnnouncement>lambdaQuery()
				.eq(BizAnnouncement::getDelFlag, "0");

		if (teachingId != null) {
			// 查课程级 + 系统级（collegeId 和 teachingId 都为空的）
			wrapper.and(w -> w
					.eq(BizAnnouncement::getTeachingId, teachingId)
					.or(o -> o.isNull(BizAnnouncement::getTeachingId).isNull(BizAnnouncement::getCollegeId)));
		} else if (collegeId != null) {
			// 查学院级 + 系统级
			wrapper.and(w -> w
					.eq(BizAnnouncement::getCollegeId, collegeId)
					.or(o -> o.isNull(BizAnnouncement::getTeachingId).isNull(BizAnnouncement::getCollegeId)));
		}
		// 都不传则查全部

		wrapper.orderByDesc(BizAnnouncement::getCreateTime);
		return this.page(page, wrapper);
	}

	@Override
	public BizAnnouncement getDetail(Long announcementId) {
		BizAnnouncement announcement = this.getById(announcementId);
		if (announcement == null || "1".equals(announcement.getDelFlag())) {
			throw new CheckedException("公告不存在");
		}
		return announcement;
	}

	@Override
	public Boolean updateAnnouncement(BizAnnouncement announcement) {
		BizAnnouncement existing = this.getById(announcement.getAnnouncementId());
		if (existing == null || "1".equals(existing.getDelFlag())) {
			throw new CheckedException("公告不存在");
		}
		announcement.setUpdateTime(new Date());
		return this.updateById(announcement);
	}

	@Override
	public Boolean removeAnnouncement(Long announcementId) {
		BizAnnouncement existing = this.getById(announcementId);
		if (existing == null || "1".equals(existing.getDelFlag())) {
			throw new CheckedException("公告不存在");
		}
		existing.setDelFlag("1");
		existing.setUpdateTime(new Date());
		return this.updateById(existing);
	}
}
