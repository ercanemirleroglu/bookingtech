package com.shameless.bookingtech.app.model;

import com.shameless.bookingtech.common.util.StringUtil;
import com.shameless.bookingtech.integration.automation.model.SearchCriteriaExtDto;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public class PriceEmailModel {
    private final Integer adult;
    private final Integer child;
    private final Integer room;
    private final String location;
    private final String currency;
    private final String currencySymbol;
    private final String fromDate;
    private final String toDate;
    private final String reportDateTime;
    private PriceReportModel increasedTable;
    private PriceReportModel decreasedTable;
    private PriceReportModel newTable;

    public PriceEmailModel(List<PriceReportModel> priceReportModelList, SearchCriteriaExtDto searchResultExtDto) {
        this.adult = searchResultExtDto.getAdult();
        this.child = searchResultExtDto.getChild();
        this.room = searchResultExtDto.getRoom();
        this.location = searchResultExtDto.getLocation();
        this.currency = searchResultExtDto.getCurrency();
        this.currencySymbol = StringUtil.getCurrencySymbol(searchResultExtDto.getCurrency());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.fromDate = searchResultExtDto.getDateRange().getStartDate().format(dateTimeFormatter);
        this.toDate = searchResultExtDto.getDateRange().getEndDate().format(dateTimeFormatter);
        if (!priceReportModelList.isEmpty()) {
            this.increasedTable = priceReportModelList.stream().filter(prm -> PriceStatus.INCREASED.equals(prm.getPriceStatus()))
                    .findFirst().orElse(null);
            this.decreasedTable = priceReportModelList.stream().filter(prm -> PriceStatus.DECREASED.equals(prm.getPriceStatus()))
                    .findFirst().orElse(null);
            this.newTable = priceReportModelList.stream().filter(prm -> PriceStatus.NEW.equals(prm.getPriceStatus()))
                    .findFirst().orElse(null);
        }
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");
        this.reportDateTime = LocalDateTime.now().format(dateTimeFormatter);
    }
}
