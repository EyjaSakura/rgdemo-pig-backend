package com.pig4cloud.pig.homework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.homework.service.BizAuditLogService;
import com.pig4cloud.pig.homework.entity.BizAuditLog;
import com.pig4cloud.pig.homework.mapper.BizAuditLogMapper;
import org.springframework.stereotype.Service;

/**
 * 操作审计日志 Service 实现类
 *
 * @author EyjaSakura
 */
@Service
public class BizAuditLogServiceImpl extends ServiceImpl<BizAuditLogMapper, BizAuditLog>
        implements BizAuditLogService {
}
