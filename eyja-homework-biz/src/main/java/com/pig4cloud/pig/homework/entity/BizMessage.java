package com.pig4cloud.pig.homework.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 消息表
 *
 * @author EyjaSakura
 * @since 2026-04-08 20:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "消息表")
public class BizMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    @Schema(description = "消息ID")
    @TableId(type = IdType.AUTO)
    private Long messageId;
    @Schema(description = "接收人ID（关联 sys_user.user_id）")
    private Long receiverId;
    @Schema(description = "消息标题")
    private String title;
    @Schema(description = "消息内容")
    private String content;
    @Schema(description = "消息类型 (0系统通知 1作业提醒 2批改通知 3课程通知)")
    private String type;
    @Schema(description = "是否已读 (0未读 1已读)")
    private Integer isRead;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "逻辑删除标记")
    private String delFlag;
}
