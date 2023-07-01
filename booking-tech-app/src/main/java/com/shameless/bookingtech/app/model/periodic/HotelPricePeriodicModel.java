package com.shameless.bookingtech.app.model.periodic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelPricePeriodicModel {
    private String hotelName;
    private List<PriceByDateRangeModel> prices;
}
