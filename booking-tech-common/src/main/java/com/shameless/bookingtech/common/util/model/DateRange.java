package com.shameless.bookingtech.common.util.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.temporal.Temporal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DateRange<T extends Temporal> {
    private T startDate;
    private T endDate;

}
