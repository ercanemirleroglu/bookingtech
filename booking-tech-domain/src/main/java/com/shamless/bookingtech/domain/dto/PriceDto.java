package com.shamless.bookingtech.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceDto {
    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private AppMoneyDto currentPrice;
    private AppMoneyDto previousPrice;
    private HotelDto hotel;
    private Integer paramAdult;
    private Integer paramChild;
    private Integer paramRoom;
}
