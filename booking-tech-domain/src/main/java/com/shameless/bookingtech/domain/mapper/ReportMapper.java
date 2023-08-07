package com.shameless.bookingtech.domain.mapper;

import com.shameless.bookingtech.domain.dto.ReportDto;
import com.shameless.bookingtech.domain.entity.ReportEntity;

public class ReportMapper {
    public static final ReportMapper INSTANCE = new ReportMapper();

    public ReportDto toDto(ReportEntity entity) {
        return ReportDto.builder()
                .id(entity.getId())
                .createdDate(entity.getCreatedDate())
                .lastModifiedDate(entity.getLastModifiedDate())
                .day(entity.getDay())
                .lastReportDate(entity.getLastReportDate())
                .lastPriceDay(entity.getLastPriceDay())
                .reportType(entity.getReportType().toDto())
                .toDate(entity.getToDate())
                .fromDate(entity.getFromDate())
                .build();
    }



}
