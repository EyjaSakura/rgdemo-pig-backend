package com.pig4cloud.pig.homework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.homework.service.BizLearningRecordService;
import com.pig4cloud.pig.homework.entity.BizLearningRecord;
import com.pig4cloud.pig.homework.mapper.BizLearningRecordMapper;
import org.springframework.stereotype.Service;

/**
 * 学习记录 Service 实现类
 *
 * @author EyjaSakura
 */
@Service
public class BizLearningRecordServiceImpl extends ServiceImpl<BizLearningRecordMapper, BizLearningRecord>
        implements BizLearningRecordService {
}
