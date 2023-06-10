package com.shameless.bookingtech.domain.dto;

import com.shameless.bookingtech.common.util.model.Param;
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
    private Param key;
    private String value;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
