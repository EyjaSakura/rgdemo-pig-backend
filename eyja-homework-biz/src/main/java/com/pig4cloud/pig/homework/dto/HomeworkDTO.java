package com.pig4cloud.pig.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "教师发布作业 DTO")
public class HomeworkDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull(message = "所属课程不能为空")
	@Schema(description = "所属课程ID", requiredMode = Schema.RequiredMode.REQUIRED)
	private Long courseId;

	@NotBlank(message = "作业名称不能为空")
	@Schema(description = "作业名称", requiredMode = Schema.RequiredMode.REQUIRED)
	private String title;

	@Schema(description = "作业描述")
	private String description;

	@Schema(description = "附件地址(多文件用逗号分隔)")
	private String attachmentUrl;

	@NotNull(message = "发布时间不能为空")
	@Schema(description = "发布时间", requiredMode = Schema.RequiredMode.REQUIRED)
	private LocalDateTime publishTime;

	@NotNull(message = "截止时间不能为空")
	@Schema(description = "截止时间", requiredMode = Schema.RequiredMode.REQUIRED)
	private LocalDateTime deadline;

}
