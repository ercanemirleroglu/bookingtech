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
public class LocationDto {
    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String name;
}
