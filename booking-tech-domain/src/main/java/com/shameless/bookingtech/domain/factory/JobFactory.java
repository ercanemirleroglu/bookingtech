package com.shameless.bookingtech.domain.factory;

import com.shameless.bookingtech.domain.dto.JobDto;
import com.shameless.bookingtech.domain.entity.JobEntity;
import com.shameless.bookingtech.domain.entity.JobStatus;
import com.shameless.bookingtech.domain.entity.TriggerType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JobFactory {
    public JobEntity from(JobDto dto) {
        return JobEntity.builder()
                .name(dto.getName())
                .lastProcessAction(dto.getLastProcessAction())
                .lastProcessDateTime(dto.getLastProcessDateTime())
                .lastTriggerDateTime(dto.getLastTriggerDateTime())
                .permissionSync(dto.isPermissionSync())
                .status(Objects.nonNull(dto.getStatus()) ?
                        JobStatus.valueOf(dto.getStatus().name()) : null)
                .triggerType(Objects.nonNull(dto.getTriggerType()) ?
                        TriggerType.valueOf(dto.getTriggerType().name()) : null)
                .error(dto.getError())
                .build();
    }
}
