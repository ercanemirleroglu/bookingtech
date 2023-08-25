package com.shameless.bookingtech.app.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class HotelWithPriceInfoModel {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yy HH:mm")
    private LocalDateTime lastModifiedDate;
    private String name;
    private Double rating;
    private BigDecimal lastPrice;
    private String location;
}
