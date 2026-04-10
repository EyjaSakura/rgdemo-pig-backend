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
 * 通知公告表
 *
 * @author EyjaSakura
 * @since 2026-04-08 20:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通知公告表")
public class BizAnnouncement implements Serializable {

    private static final long serialVersionUID = 1L;
    @Schema(description = "公告ID")
    @TableId(type = IdType.AUTO)
    private Long announcementId;
    @Schema(description = "任课ID（课程级公告，NULL则不是）")
    private Long teachingId;
    @Schema(description = "学院ID（学院级公告，NULL则不是）")
    private Long collegeId;
    @Schema(description = "公告标题")
    private String title;
    @Schema(description = "公告内容")
    private String content;
    @Schema(description = "创建人")
    private String createBy;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "修改时间")
    private Date updateTime;
    @Schema(description = "逻辑删除标记")
    private String delFlag;
}
