package com.shameless.bookingtech.app.model.response;

import com.shameless.bookingtech.app.model.HotelWithPriceInfoModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelListResponse {
    List<HotelWithPriceInfoModel> hotels;
}
