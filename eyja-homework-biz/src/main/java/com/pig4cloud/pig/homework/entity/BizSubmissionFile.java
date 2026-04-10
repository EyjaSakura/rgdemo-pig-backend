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
 * 提交附件表
 *
 * @author EyjaSakura
 * @since 2026-04-08 20:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "提交附件表")
public class BizSubmissionFile implements Serializable {

    private static final long serialVersionUID = 1L;
    @Schema(description = "文件ID")
    @TableId(type = IdType.AUTO)
    private Long fileId;
    @Schema(description = "关联提交记录ID")
    private Long submissionId;
    @Schema(description = "原始文件名")
    private String fileName;
    @Schema(description = "文件存储地址/OSS路径")
    private String fileUrl;
    @Schema(description = "文件大小（字节）")
    private Long fileSize;
    @Schema(description = "文件类型（如 pdf, docx, jpg 等）")
    private String fileType;
    @Schema(description = "上传时间")
    private Date uploadTime;
    @Schema(description = "逻辑删除标记")
    private String delFlag;
}
