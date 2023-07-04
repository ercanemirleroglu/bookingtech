package com.shameless.bookingtech.app.model;

import com.shameless.bookingtech.common.util.StringUtil;
import com.shameless.bookingtech.common.util.model.DateRange;
import com.shameless.bookingtech.integration.automation.model.SearchCriteriaExtDto;
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

    public EmailParamModel(SearchCriteriaExtDto searchCriteria, DateRange<LocalDate> dateRange){
        this.adult = searchCriteria.getAdult();
        this.child = searchCriteria.getChild();
        this.room = searchCriteria.getRoom();
        this.location = searchCriteria.getLocation();
        this.currency = searchCriteria.getCurrency();
        this.currencySymbol = StringUtil.getCurrencySymbol(searchCriteria.getCurrency());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.fromDate = dateRange.getStartDate().format(dateTimeFormatter);
        this.toDate = dateRange.getEndDate().format(dateTimeFormatter);
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        this.reportDateTime = LocalDateTime.now().format(dateTimeFormatter);
    }
}
