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
 * 选课表
 *
 * @author EyjaSakura
 * @since 2026-04-08 20:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "选课表")
public class BizEnrollment implements Serializable {

    private static final long serialVersionUID = 1L;
    @Schema(description = "选课记录ID")
    @TableId(type = IdType.AUTO)
    private Long enrollmentId;
    @Schema(description = "任课ID（关联 biz_teaching.teaching_id）")
    private Long teachingId;
    @Schema(description = "学生ID（关联 sys_user.user_id）")
    private Long studentId;
    @Schema(description = "选课时间")
    private Date enrollTime;
    @Schema(description = "选课状态 (0正常 1已退课)")
    private String status;
    @Schema(description = "逻辑删除标记")
    private String delFlag;
}
