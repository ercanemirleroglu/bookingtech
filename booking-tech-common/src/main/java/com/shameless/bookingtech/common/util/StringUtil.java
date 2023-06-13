package com.shameless.bookingtech.common.util;

import org.javamoney.moneta.format.CurrencyStyle;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.util.Locale;

public class StringUtil {
    public static String returnJustDigits(String str) {
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isDigit(c))
                digits.append(c);
        }
        return digits.toString();
    }

    public static String getCurrencySymbol(String currency) {
        CurrencyUnit gbpCurrency = Monetary.getCurrency(currency);
        MonetaryAmountFormat amountFormat = MonetaryFormats.getAmountFormat(AmountFormatQueryBuilder.of(Locale.getDefault()).set(CurrencyStyle.SYMBOL).build());
        MonetaryAmount amount = Monetary.getDefaultAmountFactory().setCurrency(gbpCurrency).setNumber(1).create();
        return amountFormat.format(amount).substring(0, 1);
    }
}
