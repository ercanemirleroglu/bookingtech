package com.shameless.bookingtech.app.rest.adapter;

import com.shameless.bookingtech.app.model.LocationModel;
import com.shameless.bookingtech.app.model.response.LocationListResponse;
import com.shameless.bookingtech.domain.dto.*;
import com.shameless.bookingtech.domain.service.LocationService;
import com.shameless.bookingtech.domain.service.SearchCriteriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LocationAdapter {
    private final LocationService locationService;
    private final SearchCriteriaService searchCriteriaService;

    public LocationListResponse listLocations() {
        SearchCriteriaDto scDto = searchCriteriaService.getCriteriaByParams();
        List<LocationDto> locationDtoList = locationService.findAllWithStatistics();
        List<LocationModel> locationModels = locationDtoList.stream()
                .map(locationDto -> {
                    LocationModel.LocationModelBuilder builder = LocationModel.builder();
                    builder.location(locationDto.getName());
                    if (Objects.nonNull(locationDto.getHotels())) {
                        locationDto.getHotels().stream().max(Comparator.comparing(HotelDto::getRating))
                                .ifPresent(hotel ->
                                        builder.highestRatingHotel(hotel.getName())
                                                .highestRatingValue(hotel.getRating()));

                        locationDto.getHotels().stream().map(HotelDto::getPrices)
                                .flatMap(Collection::stream)
                                .filter(price -> StoreTypeDto.HOURLY.equals(price.getStoreType()) &&
                                        scDto.getId().equals(price.getSearchCriteria().getId()) &&
                                        Objects.nonNull(price.getCurrentPrice()))
                                .max(Comparator.comparing(PriceDto::getCurrentPrice))
                                .ifPresent(price ->
                                        builder.highestPriceHotel(price.getHotel().getName())
                                                .highestPriceValue(price.getCurrentPrice()));

                        locationDto.getHotels().stream().map(HotelDto::getPrices)
                                .flatMap(Collection::stream)
                                .filter(price -> StoreTypeDto.HOURLY.equals(price.getStoreType()) &&
                                        scDto.getId().equals(price.getSearchCriteria().getId()) &&
                                        Objects.nonNull(price.getCurrentPrice()))
                                .min(Comparator.comparing(PriceDto::getCurrentPrice))
                                .ifPresent(price ->
                                        builder.lowestPriceHotel(price.getHotel().getName())
                                                .lowestPriceValue(price.getCurrentPrice()));

                    }
                    return builder.build();
                }).collect(Collectors.toList());
        return LocationListResponse.builder()
                .locations(locationModels)
                .build();
    }
}
