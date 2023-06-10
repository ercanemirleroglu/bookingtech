package com.shameless.bookingtech.domain.model;

import com.shameless.bookingtech.common.util.model.AppMoney;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelPriceModel {
    private String hotelName;
    private AppMoney price;
    private String location;
    private Double rating;

}
