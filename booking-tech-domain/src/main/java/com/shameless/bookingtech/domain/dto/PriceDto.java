package com.shameless.bookingtech.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceDto {
    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private BigDecimal currentPrice;
    private BigDecimal previousPrice;
    private LocalDate fromDate;
    private LocalDate toDate;
    private StoreTypeDto storeType;
    private HotelDto hotel;
    private SearchCriteriaDto searchCriteria;
}
