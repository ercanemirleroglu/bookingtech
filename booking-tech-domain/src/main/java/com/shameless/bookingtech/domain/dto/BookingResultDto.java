package com.shameless.bookingtech.domain.dto;

import com.shameless.bookingtech.domain.model.HotelPriceModel;
import com.shameless.bookingtech.domain.model.SearchCriteriaModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResultDto {
    List<HotelPriceModel> hotelPriceList;
    SearchCriteriaModel searchCriteria;

}
