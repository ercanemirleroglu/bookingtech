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
public class ParamDto {
    private Long id;
    private String key;
    private String value;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
