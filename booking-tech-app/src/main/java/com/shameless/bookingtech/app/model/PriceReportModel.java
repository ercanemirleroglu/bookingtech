package com.shameless.bookingtech.app.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PriceReportModel {
    private final PriceStatus priceStatus;
    private List<PriceModel> hotelPriceChangeList = new ArrayList<>();
    private BigDecimal averageChangeValue;
    private BigDecimal averageChangePercent;
    private final String changeSymbol;

    public PriceReportModel(List<PriceModel> priceModelList) {
        this.hotelPriceChangeList = priceModelList.stream().sorted(Comparator.comparing(PriceModel::getCurrentValue)).collect(Collectors.toList());
        this.priceStatus = priceModelList.get(0).getPriceStatus();
        this.changeSymbol = priceModelList.get(0).getChangeSymbol();
        if (List.of(PriceStatus.DECREASED, PriceStatus.INCREASED).contains(this.priceStatus)) {
            BigDecimal totalChange = BigDecimal.ZERO;
            BigDecimal totalCurrent = BigDecimal.ZERO;
            BigDecimal totalPrevious = BigDecimal.ZERO;
            for (PriceModel pm : priceModelList) {
                totalChange =  totalChange.add(pm.getChangeValue());
                totalCurrent = totalCurrent.add(pm.getCurrentValue());
                totalPrevious = totalPrevious.add(pm.getPreviousValue());
            }
            BigDecimal totalDifference = totalCurrent.subtract(totalPrevious).abs();
            BigDecimal multiply = totalDifference.multiply(BigDecimal.valueOf(100D));
            this.averageChangePercent =  multiply.divide(totalPrevious, RoundingMode.HALF_EVEN);
            this.averageChangeValue = totalChange.divide(BigDecimal.valueOf(priceModelList.size()), RoundingMode.HALF_EVEN);
        }
    }
}
