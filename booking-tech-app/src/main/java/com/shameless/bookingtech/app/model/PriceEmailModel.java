package com.shameless.bookingtech.app.model;

import com.shameless.bookingtech.common.util.model.DateRange;
import com.shameless.bookingtech.domain.dto.SearchCriteriaDto;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class PriceEmailModel {
    private final EmailParamModel emailParam;
    private PriceReportModel increasedTable;
    private PriceReportModel decreasedTable;
    private PriceReportModel newTable;

    public PriceEmailModel(List<PriceReportModel> priceReportModelList, SearchCriteriaDto searchCriteria, DateRange<LocalDate> dateRange) {
        this.emailParam = new EmailParamModel(searchCriteria, dateRange);
        if (!priceReportModelList.isEmpty()) {
            this.increasedTable = priceReportModelList.stream().filter(prm -> PriceStatus.INCREASED.equals(prm.getPriceStatus()))
                    .findFirst().orElse(null);
            this.decreasedTable = priceReportModelList.stream().filter(prm -> PriceStatus.DECREASED.equals(prm.getPriceStatus()))
                    .findFirst().orElse(null);
            this.newTable = priceReportModelList.stream().filter(prm -> PriceStatus.NEW.equals(prm.getPriceStatus()))
                    .findFirst().orElse(null);
        }
    }
}
