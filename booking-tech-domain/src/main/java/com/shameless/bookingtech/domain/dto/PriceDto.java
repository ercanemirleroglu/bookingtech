package com.shameless.bookingtech.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    private String currentCurrency;
    private BigDecimal previousPrice;
    private String previousCurrency;
    private HotelDto hotel;
    private SearchCriteriaDto searchCriteria;
}
