package com.shamless.bookingtech.app.service;

import com.shamless.bookingtech.common.util.model.Param;
import com.shamless.bookingtech.integration.automation.model.HotelPriceExtDto;
import com.shamless.bookingtech.integration.automation.service.BookingService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@EnableScheduling
public class ProcessService {

    private final BookingService bookingService;

    public ProcessService(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Scheduled(fixedRate = 20 * 60 * 1000)
    public void checkServices() throws InterruptedException {
        Map<Param, String> params = new HashMap<>();
        params.put(Param.APP_CURRENCY_UNIT, "GBP");
        params.put(Param.APP_LANGUAGE, "English (US)");
        params.put(Param.SEARCH_LOCATION, "Norwich, United Kingdom");
        params.put(Param.SEARCH_ADULT, "2");
        params.put(Param.SEARCH_CHILD, "0");
        params.put(Param.SEARCH_ROOM, "1");
        params.put(Param.SEARCH_DATE_RANGE, "1");
        List<HotelPriceExtDto> hotelPriceExtDtos = bookingService.fetchBookingData(params);
    }
}
