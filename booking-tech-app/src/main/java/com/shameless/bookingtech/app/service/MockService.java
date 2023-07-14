package com.shameless.bookingtech.app.service;

import com.shameless.bookingtech.common.util.model.AppMoney;
import com.shameless.bookingtech.common.util.model.DateRange;
import com.shameless.bookingtech.integration.automation.model.HotelPriceExtDto;
import com.shameless.bookingtech.integration.automation.model.PeriodicResultExtDto;
import com.shameless.bookingtech.integration.automation.model.SearchCriteriaExtDto;
import com.shameless.bookingtech.integration.automation.model.SearchResultExtDto;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Component;

import javax.money.Monetary;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

@Component
public class MockService {

    private static final String CURRENCY = "GBP";
    private static final String LOCATION = "Norwich";

    public SearchResultExtDto createSearchResultExtDtoMock() {
        SearchCriteriaExtDto searchCriteriaExtDto = SearchCriteriaExtDto.builder()
                .dayRange(1)
                .currency(CURRENCY)
                .room(1)
                .adult(2)
                .child(0)
                .location("Norwich, United Kingdom")
                .build();

        LocalDate today = LocalDate.now();

        Money money = Money.of(64, Monetary.getCurrency(CURRENCY));
        AppMoney price = new AppMoney(money);
        HotelPriceExtDto hotelPriceExtDto1 = HotelPriceExtDto.builder()
                .hotelName("Hotel California")
                .location(LOCATION)
                .rating(6.5)
                .price(price)
                .build();

        money = Money.of(55, Monetary.getCurrency(CURRENCY));
        price = new AppMoney(money);
        HotelPriceExtDto hotelPriceExtDto2 = HotelPriceExtDto.builder()
                .hotelName("Kardeshler Hotel")
                .location(LOCATION)
                .rating(8.2)
                .price(price)
                .build();

        money = Money.of(92, Monetary.getCurrency(CURRENCY));
        price = new AppMoney(money);
        HotelPriceExtDto hotelPriceExtDto3 = HotelPriceExtDto.builder()
                .hotelName("Yeni Hotel")
                .location(LOCATION)
                .rating(8.9)
                .price(price)
                .build();

        PeriodicResultExtDto periodicResultExtDto = PeriodicResultExtDto.builder()
                .dateRange(new DateRange<>(today, today.plusDays(1)))
                .hotelPriceList(Arrays.asList(hotelPriceExtDto1, hotelPriceExtDto3))
                .build();

        return SearchResultExtDto.builder()
                .searchCriteria(searchCriteriaExtDto)
                .periodicResultList(Collections.singletonList(periodicResultExtDto))
                .build();
    }
}
