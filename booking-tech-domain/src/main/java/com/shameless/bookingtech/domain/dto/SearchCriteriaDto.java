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
public class SearchCriteriaDto {
    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private Integer paramAdult;
    private Integer paramChild;
    private Integer paramRoom;
    private String paramLocation;
    private String paramCurrency;
}
