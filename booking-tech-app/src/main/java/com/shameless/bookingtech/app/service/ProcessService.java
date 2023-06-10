package com.shameless.bookingtech.app.service;

import com.shameless.bookingtech.common.util.model.Param;
import com.shameless.bookingtech.domain.dto.BookingResultDto;
import com.shameless.bookingtech.domain.model.HotelPriceModel;
import com.shameless.bookingtech.domain.model.SearchCriteriaModel;
import com.shameless.bookingtech.domain.service.HotelApplicationService;
import com.shameless.bookingtech.integration.automation.model.HotelPriceExtDto;
import com.shameless.bookingtech.integration.automation.model.SearchCriteriaExtDto;
import com.shameless.bookingtech.integration.automation.model.SearchResultExtDto;
import com.shameless.bookingtech.integration.automation.service.BookingService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@EnableScheduling
public class ProcessService {

    private final BookingService bookingService;
    private final HotelApplicationService hotelApplicationService;

    public ProcessService(BookingService bookingService, HotelApplicationService hotelApplicationService) {
        this.bookingService = bookingService;
        this.hotelApplicationService = hotelApplicationService;
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
        SearchResultExtDto searchResultExtDto = bookingService.fetchBookingData(params);
        hotelApplicationService.save(toDto(searchResultExtDto));

    }

    private BookingResultDto toDto(SearchResultExtDto searchResultExtDto) {
        return BookingResultDto.builder()
                .searchCriteria(mapSearchCriteria(searchResultExtDto.getSearchCriteria()))
                .hotelPriceList(mapHotelPriceList(searchResultExtDto.getHotelPriceList()))
                .build();
    }

    private List<HotelPriceModel> mapHotelPriceList(List<HotelPriceExtDto> hotelPriceList) {
        return hotelPriceList.stream().map(hotelPriceExtDto ->
            HotelPriceModel.builder()
                    .hotelName(hotelPriceExtDto.getHotelName())
                    .price(hotelPriceExtDto.getPrice())
                    .location(hotelPriceExtDto.getLocation())
                    .rating(hotelPriceExtDto.getRating())
                    .build()).collect(Collectors.toList());
    }

    private SearchCriteriaModel mapSearchCriteria(SearchCriteriaExtDto searchCriteria) {
        return SearchCriteriaModel.builder()
                .adult(searchCriteria.getAdult())
                .child(searchCriteria.getChild())
                .room(searchCriteria.getRoom())
                .location(searchCriteria.getLocation())
                .dateRange(searchCriteria.getDateRange())
                .build();
    }
}
