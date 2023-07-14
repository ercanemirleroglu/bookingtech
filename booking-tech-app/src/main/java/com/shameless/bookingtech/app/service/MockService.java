package com.shameless.bookingtech.app.service;

import com.shameless.bookingtech.common.util.JsonUtil;
import com.shameless.bookingtech.common.util.model.DateRange;
import com.shameless.bookingtech.integration.automation.model.HotelPriceExtDto;
import com.shameless.bookingtech.integration.automation.model.PeriodicResultExtDto;
import com.shameless.bookingtech.integration.automation.model.SearchCriteriaExtDto;
import com.shameless.bookingtech.integration.automation.model.SearchResultExtDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
public class MockService {

    private static final String CURRENCY = "GBP";

    public SearchResultExtDto createSearchResultExtDtoMock() throws IOException {
        SearchCriteriaExtDto searchCriteriaExtDto = SearchCriteriaExtDto.builder()
                .dayRange(1)
                .currency(CURRENCY)
                .room(1)
                .adult(2)
                .child(0)
                .location("Norwich, United Kingdom")
                .build();

        LocalDate today = LocalDate.now();

        Resource resource = new ClassPathResource("hotels.json");
        String filePath = resource.getFile().getAbsolutePath();
        HotelPriceExtDtoScope hotelPriceExtDtoScope = (HotelPriceExtDtoScope) JsonUtil.getInstance()
                .readFile(HotelPriceExtDtoScope.class, filePath, "hotels");

        PeriodicResultExtDto periodicResultExtDto = PeriodicResultExtDto.builder()
                .dateRange(new DateRange<>(today, today.plusDays(1)))
                .hotelPriceList(hotelPriceExtDtoScope.getHotelPrices())
                .build();

        return SearchResultExtDto.builder()
                .searchCriteria(searchCriteriaExtDto)
                .periodicResultList(Collections.singletonList(periodicResultExtDto))
                .build();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class HotelPriceExtDtoScope {
        private List<HotelPriceExtDto> hotelPrices;
    }
}
