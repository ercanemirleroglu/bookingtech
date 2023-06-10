package com.shameless.bookingtech.integration.automation.model;

import com.shameless.bookingtech.common.util.model.AppMoney;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelPriceExtDto {
    private String hotelName;
    private AppMoney price;
    private String location;
    private Double rating;

}
