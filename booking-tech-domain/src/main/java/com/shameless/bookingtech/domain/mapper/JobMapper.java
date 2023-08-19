package com.shameless.bookingtech.domain.mapper;

import com.shameless.bookingtech.domain.dto.JobDto;
import com.shameless.bookingtech.domain.entity.JobEntity;

public class JobMapper {
    public static final JobMapper INSTANCE = new JobMapper();

    public JobDto toDto(JobEntity entity) {
        return JobDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .lastProcessAction(entity.getLastProcessAction())
                .lastProcessDateTime(entity.getLastProcessDateTime())
                .lastTriggerDateTime(entity.getLastTriggerDateTime())
                .status(entity.getStatus().toDto())
                .triggerType(entity.getTriggerType().toDto())
                .error(entity.getError())
                .permissionSync(entity.isPermissionSync())
                .build();
    }



}
