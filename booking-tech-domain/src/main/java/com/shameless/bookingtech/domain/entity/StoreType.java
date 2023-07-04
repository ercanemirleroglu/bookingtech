package com.shameless.bookingtech.domain.entity;

import com.shameless.bookingtech.domain.dto.StoreTypeDto;

public enum StoreType {
    HOURLY, PERIODIC;

    public StoreTypeDto toDto() {
        return StoreTypeDto.valueOf(this.name());
    }
}
