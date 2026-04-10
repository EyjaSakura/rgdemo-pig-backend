package com.pig4cloud.pig.homework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.homework.service.BizSubmissionFileService;
import com.pig4cloud.pig.homework.entity.BizSubmissionFile;
import com.pig4cloud.pig.homework.mapper.BizSubmissionFileMapper;
import org.springframework.stereotype.Service;

/**
 * 提交附件 Service 实现类
 *
 * @author EyjaSakura
 */
@Service
public class BizSubmissionFileServiceImpl extends ServiceImpl<BizSubmissionFileMapper, BizSubmissionFile>
        implements BizSubmissionFileService {
}
