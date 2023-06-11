package com.shameless.bookingtech.domain.service;

import com.shameless.bookingtech.domain.dto.BookingResultDto;
import com.shameless.bookingtech.domain.dto.HotelDto;
import com.shameless.bookingtech.domain.dto.SearchCriteriaDto;
import com.shameless.bookingtech.domain.model.HotelPriceModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class HotelApplicationService {
    private final SearchCriteriaService searchCriteriaService;
    private final LocationService locationService;
    private final HotelService hotelService;

    public HotelApplicationService(SearchCriteriaService searchCriteriaService, LocationService locationService, HotelService hotelService) {
        this.searchCriteriaService = searchCriteriaService;
        this.locationService = locationService;
        this.hotelService = hotelService;
    }

    @Transactional
    public void save(BookingResultDto bookingResultDto) {
        SearchCriteriaDto searchCriteriaDto = SearchCriteriaDto.builder()
                .toDate(bookingResultDto.getSearchCriteria().getDateRange().getEndDate())
                .fromDate(bookingResultDto.getSearchCriteria().getDateRange().getStartDate())
                .paramAdult(bookingResultDto.getSearchCriteria().getAdult())
                .paramChild(bookingResultDto.getSearchCriteria().getChild())
                .paramRoom(bookingResultDto.getSearchCriteria().getRoom())
                .paramLocation(bookingResultDto.getSearchCriteria().getLocation())
                .build();

        SearchCriteriaDto byParams = searchCriteriaService.findByParams(searchCriteriaDto);
        if (Objects.isNull(byParams))
            searchCriteriaService.add(searchCriteriaDto);
        else
            searchCriteriaService.update(searchCriteriaDto);

        List<String> locations = bookingResultDto.getHotelPriceList().stream()
                .map(HotelPriceModel::getLocation).distinct().collect(Collectors.toList());

        locationService.addBulk(locations);

        List<HotelDto> hotelDtoList = bookingResultDto.getHotelPriceList().stream().map(hotelPriceModel ->
                HotelDto.builder()
                        .location(locationService.getByName(hotelPriceModel.getLocation()))
                        .name(hotelPriceModel.getHotelName())
                        .rating(hotelPriceModel.getRating())
                        .build()).collect(Collectors.toList());

        List<HotelDto> hotelDtoListInThisProcess = hotelService.addBulk(hotelDtoList);


    }
}
