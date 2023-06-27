package com.shameless.bookingtech.domain.service;

import com.shameless.bookingtech.domain.dto.*;
import com.shameless.bookingtech.domain.model.HotelPriceModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class HotelApplicationService {
    private final SearchCriteriaService searchCriteriaService;
    private final LocationService locationService;
    private final HotelService hotelService;
    private final PriceService priceService;

    public HotelApplicationService(SearchCriteriaService searchCriteriaService, LocationService locationService, HotelService hotelService, PriceService priceService) {
        this.searchCriteriaService = searchCriteriaService;
        this.locationService = locationService;
        this.hotelService = hotelService;
        this.priceService = priceService;
    }

    @Transactional
    public List<PriceDto> save(BookingResultDto bookingResultDto) {
        SearchCriteriaDto searchCriteriaDto = SearchCriteriaDto.builder()
                .paramAdult(bookingResultDto.getSearchCriteria().getAdult())
                .paramChild(bookingResultDto.getSearchCriteria().getChild())
                .paramRoom(bookingResultDto.getSearchCriteria().getRoom())
                .paramLocation(bookingResultDto.getSearchCriteria().getLocation())
                .paramCurrency(bookingResultDto.getSearchCriteria().getCurrency())
                .build();

        SearchCriteriaDto byParams = searchCriteriaService.findByParams(searchCriteriaDto);
        if (Objects.isNull(byParams))
            byParams = searchCriteriaService.add(searchCriteriaDto);

        List<HotelPriceModel> hotelPriceModelList = bookingResultDto.getPeriodicHotelPriceList().stream()
                .map(PeriodicHotelPriceModel::getHotelPriceList)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<String> locations = hotelPriceModelList.stream()
                .map(HotelPriceModel::getLocation).distinct().collect(Collectors.toList());

        locationService.addBulk(locations);

        List<HotelDto> hotelDtoList = hotelPriceModelList.stream().map(hotelPriceModel ->
                        HotelDto.builder()
                                .location(locationService.getByName(hotelPriceModel.getLocation()))
                                .name(hotelPriceModel.getHotelName())
                                .rating(hotelPriceModel.getRating())
                                .build()).collect(Collectors.toList());

        hotelService.addBulk(hotelDtoList);

        return priceService.setAllPrices(bookingResultDto.getPeriodicHotelPriceList(), byParams.getId());
    }
}
