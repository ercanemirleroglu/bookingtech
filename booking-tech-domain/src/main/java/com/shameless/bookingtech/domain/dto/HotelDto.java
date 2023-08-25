package com.shameless.bookingtech.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelDto {
    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String name;
    private Double rating;
    private LocationDto location;
    private List<PriceDto> prices;
}
