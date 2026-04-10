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
 * 任课表（核心表）
 *
 * @author EyjaSakura
 * @since 2026-04-08 20:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "任课表（核心表）")
public class BizTeaching implements Serializable {

    private static final long serialVersionUID = 1L;
    @Schema(description = "任课ID（代理主键）")
    @TableId(type = IdType.AUTO)
    private Long teachingId;
    @Schema(description = "课程号（关联 biz_course.course_code）")
    private String courseCode;
    @Schema(description = "教学年份（如 2026）")
    private Integer teachYear;
    @Schema(description = "课序号（01、02...）")
    private String classNo;
    @Schema(description = "授课教师ID（关联 sys_user.user_id）")
    private Long teacherId;
    @Schema(description = "开课学院ID（冗余，方便查询；关联 biz_college）")
    private Long collegeId;
    @Schema(description = "选课人数上限")
    private Integer maxStudents;
    @Schema(description = "上课时间与地点")
    private String timePlace;
    @Schema(description = "任课状态 (0未开始 1进行中 2已结束)")
    private String status;
    @Schema(description = "当前已选课人数（冗余，用于快速判断）")
    private Integer enrolledCount;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "修改时间")
    private Date updateTime;
    @Schema(description = "逻辑删除标记 0正常 1删除")
    private String delFlag;
}
