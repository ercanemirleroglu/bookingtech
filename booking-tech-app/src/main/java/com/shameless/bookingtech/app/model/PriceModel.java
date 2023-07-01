package com.shameless.bookingtech.app.model;

import com.shameless.bookingtech.domain.dto.PriceDto;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class PriceModel {
    private final String hotelName;
    private Double rating;
    private String location;
    private BigDecimal currentValue;
    private BigDecimal previousValue;
    private PriceStatus priceStatus;
    private BigDecimal changeValue;
    private BigDecimal changePercent;
    private String changeSymbol;

    public String getChangeSymbol(){
        return changeSymbol == null ? "" : changeSymbol;
    }

    public PriceModel(String hotelName) {
        this.hotelName = hotelName;
        this.priceStatus = PriceStatus.STATIC;
    }

    public PriceModel(PriceDto priceDto) {
        this.hotelName = priceDto.getHotel().getName();
        this.rating = priceDto.getHotel().getRating();
        this.location = priceDto.getHotel().getLocation().getName();
        this.currentValue = priceDto.getCurrentPrice();
        this.previousValue = priceDto.getPreviousPrice();
        this.setPriceDetail();
    }

    private void setPriceDetail(){
        this.changeSymbol = "";
        this.changePercent = BigDecimal.ZERO;
        this.changeValue = BigDecimal.ZERO;
        if (this.previousValue == null) {
            this.priceStatus = PriceStatus.NEW;
        }
        else if (this.currentValue.compareTo(this.previousValue) > 0) {
            this.priceStatus = PriceStatus.INCREASED;
            this.changeValue = this.currentValue.subtract(this.previousValue);
            BigDecimal multiply = this.changeValue.multiply(BigDecimal.valueOf(100D));
            this.changePercent = multiply.divide(this.previousValue, RoundingMode.HALF_EVEN);
            this.changeSymbol = "+";
        }
        else if(this.currentValue.compareTo(this.previousValue) < 0) {
            this.priceStatus = PriceStatus.DECREASED;
            this.changeValue = this.previousValue.subtract(this.currentValue);
            BigDecimal abs = this.changeValue.abs();
            BigDecimal multiply = abs.multiply(BigDecimal.valueOf(100D));
            this.changePercent = multiply.divide(this.previousValue, RoundingMode.HALF_EVEN);
            this.changeSymbol = "-";
        }
        else {
            this.priceStatus = PriceStatus.STATIC;
        }
    }
}
