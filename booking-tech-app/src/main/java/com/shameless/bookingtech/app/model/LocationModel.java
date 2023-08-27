package com.shameless.bookingtech.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationModel {
    private String location;
    private String highestRatingHotel;
    private Double highestRatingValue;
    private String highestPriceHotel;
    private BigDecimal highestPriceValue;
    private String lowestPriceHotel;
    private BigDecimal lowestPriceValue;
}
