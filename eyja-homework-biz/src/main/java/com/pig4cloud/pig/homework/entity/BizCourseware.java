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
 * 课程资源/课件表
 *
 * @author EyjaSakura
 * @since 2026-04-08 20:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "课程资源/课件表")
public class BizCourseware implements Serializable {

    private static final long serialVersionUID = 1L;
    @Schema(description = "课件ID")
    @TableId(type = IdType.AUTO)
    private Long coursewareId;
    @Schema(description = "任课ID（关联 biz_teaching.teaching_id）")
    private Long teachingId;
    @Schema(description = "文件夹/分组名称")
    private String folderName;
    @Schema(description = "课件标题")
    private String title;
    @Schema(description = "课件文件下载地址/OSS路径")
    private String fileUrl;
    @Schema(description = "原始文件名")
    private String fileName;
    @Schema(description = "文件大小（字节）")
    private Long fileSize;
    @Schema(description = "排序权重")
    private Integer sortOrder;
    @Schema(description = "上传时间")
    private Date uploadTime;
    @Schema(description = "逻辑删除标记")
    private String delFlag;
}
