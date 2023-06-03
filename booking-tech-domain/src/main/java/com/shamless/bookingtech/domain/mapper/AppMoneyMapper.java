package com.shamless.bookingtech.domain.mapper;

import com.shamless.bookingtech.domain.dto.AppMoneyDto;
import com.shamless.bookingtech.domain.dto.HotelDto;
import com.shamless.bookingtech.domain.entity.HotelEntity;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.NumberValue;
import java.math.BigDecimal;
import java.util.Objects;

public class AppMoneyMapper {
    public static final AppMoneyMapper INSTANCE = new AppMoneyMapper();

    public AppMoneyDto toDto(MonetaryAmount monetaryAmount) {
        return AppMoneyDto.builder()
                .value((Objects.nonNull(monetaryAmount) && Objects.nonNull(monetaryAmount.getNumber())) ?
                        BigDecimal.valueOf(monetaryAmount.getNumber().doubleValue()) : null)
                .currency((Objects.nonNull(monetaryAmount) && Objects.nonNull(monetaryAmount.getCurrency())) ?
                        monetaryAmount.getCurrency().getCurrencyCode() : null)
                .build();
    }

    public MonetaryAmount toMoney(AppMoneyDto dto) {
        if (Objects.isNull(dto) || Objects.isNull(dto.getValue()) || Objects.isNull(dto.getCurrency()))
            return null;
        return Monetary.getDefaultAmountFactory()
                .setCurrency(Monetary.getCurrency(dto.getCurrency()))
                .setNumber(100.0)
                .create();
    }
}
