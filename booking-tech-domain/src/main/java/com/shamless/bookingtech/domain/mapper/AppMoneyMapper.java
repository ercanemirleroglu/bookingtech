package com.shamless.bookingtech.domain.mapper;

import com.shamless.bookingtech.domain.dto.AppMoneyDto;
import com.shamless.bookingtech.domain.dto.HotelDto;
import com.shamless.bookingtech.domain.entity.HotelEntity;

import javax.money.MonetaryAmount;
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
}
