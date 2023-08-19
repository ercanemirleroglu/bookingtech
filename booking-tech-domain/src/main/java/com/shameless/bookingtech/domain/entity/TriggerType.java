package com.shameless.bookingtech.domain.entity;

import com.shameless.bookingtech.domain.dto.TriggerTypeDto;

public enum TriggerType {
    MANUAL, SYSTEM;

    public TriggerTypeDto toDto() {
        return TriggerTypeDto.valueOf(this.name());
    }
}
