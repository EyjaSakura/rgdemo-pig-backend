package com.pig4cloud.pig.homework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.homework.entity.BizMessage;

/**
 * 消息 Service
 */
public interface BizMessageService extends IService<BizMessage> {

    /** 统计当前用户未读消息数 */
    int countUnread(Long receiverId);

    /** 标记当前用户所有消息为已读 */
    void markAllRead(Long receiverId);
}
