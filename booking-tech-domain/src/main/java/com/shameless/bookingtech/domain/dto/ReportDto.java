package com.shameless.bookingtech.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
    private Long id;
    private LocalDate day;
    private LocalDateTime lastReportDate;
    private StoreTypeDto reportType;
    private LocalDate lastPriceDay;
    private int sendCountInDay;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private LocalDate fromDate;
    private LocalDate toDate;
}
