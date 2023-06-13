package com.shameless.bookingtech.domain.model;

import com.shameless.bookingtech.common.util.model.DateRange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteriaModel {
    private int adult;
    private int room;
    private int child;
    private String location;
    private String currency;
    private DateRange<LocalDate> dateRange;
}
