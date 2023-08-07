package com.shameless.bookingtech.domain.factory;

import com.shameless.bookingtech.domain.dto.ReportDto;
import com.shameless.bookingtech.domain.entity.ReportEntity;
import com.shameless.bookingtech.domain.entity.StoreType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportFactory {
    public ReportEntity from(ReportDto dto) {
        return ReportEntity.builder()
                .day(dto.getDay())
                .lastReportDate(dto.getLastReportDate())
                .reportType(StoreType.valueOf(dto.getReportType().name()))
                .fromDate(dto.getFromDate())
                .toDate(dto.getToDate())
                .lastPriceDay(dto.getLastPriceDay())
                .build();
    }
}
