package com.shamless.bookingtech.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;

@Converter(autoApply = true)
public class MoneyConverter implements AttributeConverter<MonetaryAmount, String> {


    @Override
    public String convertToDatabaseColumn(MonetaryAmount monetaryAmount) {
        if (monetaryAmount == null) {
            return null;
        }

        return monetaryAmount.toString();
    }

    @Override
    public MonetaryAmount convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }

        return Money.parse(value);
    }

}
