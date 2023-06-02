package com.shamless.bookingtech.integration.automation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelPriceExtDto {
    private String hotelName;
    private BigDecimal price;
}
