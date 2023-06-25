package com.shameless.bookingtech.integration.automation.model;

import com.shameless.bookingtech.common.util.model.DateRange;
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
public class PeriodicResultExtDto {
    private List<HotelPriceExtDto> hotelPriceList;
    private DateRange<LocalDate> dateRange;

}
