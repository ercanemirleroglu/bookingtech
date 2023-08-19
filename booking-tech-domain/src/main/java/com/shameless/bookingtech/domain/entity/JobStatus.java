package com.shameless.bookingtech.domain.entity;

import com.shameless.bookingtech.domain.dto.JobStatusDto;

public enum JobStatus {
    WORKING, WAITING;

    public JobStatusDto toDto() {
        return JobStatusDto.valueOf(this.name());
    }

}
