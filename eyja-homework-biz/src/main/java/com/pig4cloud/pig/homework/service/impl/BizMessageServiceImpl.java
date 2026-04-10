package com.pig4cloud.pig.homework.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.homework.service.BizMessageService;
import com.pig4cloud.pig.homework.entity.BizMessage;
import com.pig4cloud.pig.homework.mapper.BizMessageMapper;
import org.springframework.stereotype.Service;

/**
 * 消息 Service 实现类
 *
 * @author EyjaSakura
 */
@Service
public class BizMessageServiceImpl extends ServiceImpl<BizMessageMapper, BizMessage>
        implements BizMessageService {

    @Override
    public int countUnread(Long receiverId) {
        return (int) count(Wrappers.<BizMessage>lambdaQuery()
                .eq(BizMessage::getReceiverId, receiverId)
                .eq(BizMessage::getIsRead, 0));
    }

    @Override
    public void markAllRead(Long receiverId) {
        update(Wrappers.<BizMessage>lambdaUpdate()
                .eq(BizMessage::getReceiverId, receiverId)
                .eq(BizMessage::getIsRead, 0)
                .set(BizMessage::getIsRead, 1));
    }
}
