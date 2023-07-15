package com.shameless.bookingtech.integration.automation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shameless.bookingtech.common.util.model.DateRange;
import lombok.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultExtDto {
    private List<PeriodicResultExtDto> periodicResultList;
    private SearchCriteriaExtDto searchCriteria;

    @JsonIgnore
    public DateRange<LocalDate> getPeriodicDateRange() {
        periodicResultControl();
        List<DateRange<LocalDate>> dateRangesSorted = periodicResultList.stream()
                .map(PeriodicResultExtDto::getDateRange)
                .sorted(Comparator.comparing(DateRange::getStartDate))
                .collect(Collectors.toList());
        return new DateRange<>(dateRangesSorted.get(0).getStartDate(),
                dateRangesSorted.get(dateRangesSorted.size() - 1).getEndDate());
    }

    @JsonIgnore
    public DateRange<LocalDate> getHourlyDateRange() {
        periodicResultControl();
        return periodicResultList.get(0).getDateRange();
    }

    private void periodicResultControl(){
        if (Objects.isNull(periodicResultList) || periodicResultList.isEmpty())
            throw new IllegalArgumentException("Periodic Result List is empty!");
    }

}
