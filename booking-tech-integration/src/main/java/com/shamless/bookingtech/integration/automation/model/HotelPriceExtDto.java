package com.shamless.bookingtech.integration.automation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shamless.bookingtech.common.util.model.AppMoney;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javamoney.moneta.Money;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelPriceExtDto {
    private String hotelName;
    private AppMoney price;
    private String location;
    private Double rating;

}
