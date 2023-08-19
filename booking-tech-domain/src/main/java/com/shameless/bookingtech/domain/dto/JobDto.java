package com.shameless.bookingtech.domain.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDto {
    private Long id;
    @Enumerated(EnumType.STRING)
    private JobStatusDto status;
    private LocalDateTime lastTriggerDateTime;
    private LocalDateTime lastProcessDateTime;
    private String lastProcessAction;
    @Enumerated(EnumType.STRING)
    private TriggerTypeDto triggerType;
    private String name;
    private boolean permissionSync;
    private String error;
}
