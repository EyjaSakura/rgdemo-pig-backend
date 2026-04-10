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
 * 学习记录表
 *
 * @author EyjaSakura
 * @since 2026-04-08 20:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "学习记录表")
public class BizLearningRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    @Schema(description = "记录ID")
    @TableId(type = IdType.AUTO)
    private Long recordId;
    @Schema(description = "学生ID（关联 sys_user.user_id）")
    private Long studentId;
    @Schema(description = "任课ID（关联 biz_teaching.teaching_id）")
    private Long teachingId;
    @Schema(description = "行为类型 (0课件查看 1课程访问 2作业查看)")
    private String actionType;
    @Schema(description = "关联资源ID（课件ID/作业ID等）")
    private Long resourceId;
    @Schema(description = "记录时间")
    private Date createTime;
}
