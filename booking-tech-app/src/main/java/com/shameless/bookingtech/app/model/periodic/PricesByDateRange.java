package com.shameless.bookingtech.app.model.periodic;

import com.shameless.bookingtech.app.model.PriceModel;
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
public class PricesByDateRange {
    private DateRange<LocalDate> dateRange;
    private List<PriceModel> prices;
}
