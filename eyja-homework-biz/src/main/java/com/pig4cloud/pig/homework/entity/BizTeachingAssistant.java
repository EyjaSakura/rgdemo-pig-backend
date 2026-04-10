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
 * 助教表
 *
 * @author EyjaSakura
 * @since 2026-04-08 20:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "助教表")
public class BizTeachingAssistant implements Serializable {

    private static final long serialVersionUID = 1L;
    @Schema(description = "主键")
    @TableId(type = IdType.AUTO)
    private Long id;
    @Schema(description = "任课ID（关联 biz_teaching.teaching_id）")
    private Long teachingId;
    @Schema(description = "助教学生ID（关联 sys_user.user_id）")
    private Long assistantId;
    @Schema(description = "指派教师ID（关联 sys_user.user_id）")
    private Long teacherId;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "逻辑删除标记")
    private String delFlag;
}
