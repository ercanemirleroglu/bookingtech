package com.shameless.bookingtech.app.model;

import com.shameless.bookingtech.common.util.StringUtil;
import com.shameless.bookingtech.common.util.model.DateRange;
import com.shameless.bookingtech.domain.dto.SearchCriteriaDto;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class EmailParamModel {
    private final Integer adult;
    private final Integer child;
    private final Integer room;
    private final String location;
    private final String currency;
    private final String currencySymbol;
    private final String fromDate;
    private final String toDate;
    private final String reportDateTime;

    public EmailParamModel(SearchCriteriaDto searchCriteria, DateRange<LocalDate> dateRange){
        this.adult = searchCriteria.getParamAdult();
        this.child = searchCriteria.getParamChild();
        this.room = searchCriteria.getParamChild();
        this.location = searchCriteria.getParamLocation();
        this.currency = searchCriteria.getParamCurrency();
        this.currencySymbol = StringUtil.getCurrencySymbol(searchCriteria.getParamCurrency());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.fromDate = dateRange.getStartDate().format(dateTimeFormatter);
        this.toDate = dateRange.getEndDate().format(dateTimeFormatter);
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        this.reportDateTime = LocalDateTime.now().format(dateTimeFormatter);
    }
}
