package com.shameless.bookingtech.domain.dto;

import com.shameless.bookingtech.common.util.model.DateRange;
import com.shameless.bookingtech.domain.model.HotelPriceModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeriodicHotelPriceModel {
    private List<HotelPriceModel> hotelPriceList;
    private DateRange<LocalDate> dateRange;
}
