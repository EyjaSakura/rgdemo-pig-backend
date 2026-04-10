package com.pig4cloud.pig.homework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.homework.service.BizHomeworkAttachmentService;
import com.pig4cloud.pig.homework.entity.BizHomeworkAttachment;
import com.pig4cloud.pig.homework.mapper.BizHomeworkAttachmentMapper;
import org.springframework.stereotype.Service;

/**
 * 作业附件 Service 实现类
 *
 * @author EyjaSakura
 */
@Service
public class BizHomeworkAttachmentServiceImpl extends ServiceImpl<BizHomeworkAttachmentMapper, BizHomeworkAttachment>
        implements BizHomeworkAttachmentService {
}
