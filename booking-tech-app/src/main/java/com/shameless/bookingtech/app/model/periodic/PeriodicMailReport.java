package com.shameless.bookingtech.app.model.periodic;

import com.shameless.bookingtech.app.model.EmailParamModel;
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
public class PeriodicMailReport {
    private EmailParamModel emailParam;
    private List<HotelPricePeriodicModel> rows;
    private List<DateRange<LocalDate>> columns;
}
