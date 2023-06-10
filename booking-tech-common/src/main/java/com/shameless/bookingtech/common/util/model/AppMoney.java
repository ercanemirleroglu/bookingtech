package com.shameless.bookingtech.common.util.model;

import lombok.*;
import org.javamoney.moneta.Money;

import java.math.BigDecimal;

@Getter
public class AppMoney {
    private final BigDecimal value;
    private final String currency;

    public AppMoney(Money money){
        value = BigDecimal.valueOf(money.getNumber().doubleValue());
        currency = money.getCurrency().getCurrencyCode();
    }
}
