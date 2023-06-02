package com.shamless.bookingtech.app.service;

import com.shamless.bookingtech.integration.automation.HotelPriceExtDto;
import com.shamless.bookingtech.integration.automation.service.BookingService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@EnableScheduling
public class ProcessService {

    private final BookingService bookingService;

    public ProcessService(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Scheduled(fixedRate = 20 * 60 * 1000)
    public void checkServices() throws InterruptedException {
        List<HotelPriceExtDto> hotelPriceExtDtos = bookingService.fetchBookingData();
    }
}
