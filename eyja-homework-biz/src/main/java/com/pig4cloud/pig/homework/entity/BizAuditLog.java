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
 * 操作审计日志表
 *
 * @author EyjaSakura
 * @since 2026-04-08 20:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "操作审计日志表")
public class BizAuditLog implements Serializable {

    private static final long serialVersionUID = 1L;
    @Schema(description = "日志ID")
    @TableId(type = IdType.AUTO)
    private Long logId;
    @Schema(description = "操作人ID")
    private Long userId;
    @Schema(description = "操作人用户名")
    private String username;
    @Schema(description = "操作模块")
    private String module;
    @Schema(description = "操作描述")
    private String action;
    @Schema(description = "操作详情（JSON）")
    private String detail;
    @Schema(description = "操作IP")
    private String ip;
    @Schema(description = "操作时间")
    private Date createTime;
}
