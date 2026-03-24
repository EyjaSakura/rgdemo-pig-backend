package com.pig4cloud.pig.homework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.homework.dto.HomeworkDTO;
import com.pig4cloud.pig.homework.entity.HomeworkEntity;

public interface HomeworkService extends IService<HomeworkEntity> {

	Long publishHomework(HomeworkDTO homeworkDTO);

}
