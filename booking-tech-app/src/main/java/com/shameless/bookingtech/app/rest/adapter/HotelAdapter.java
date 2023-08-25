package com.shameless.bookingtech.app.rest.adapter;

import com.shameless.bookingtech.app.model.HotelWithPriceInfoModel;
import com.shameless.bookingtech.app.model.response.HotelListResponse;
import com.shameless.bookingtech.domain.dto.HotelDto;
import com.shameless.bookingtech.domain.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HotelAdapter {
    private final HotelService hotelService;

    public HotelListResponse listHotels() {
        List<HotelDto> allWithLastPriceInfo = hotelService.findAllWithLastPriceInfo();
        List<HotelWithPriceInfoModel> hotelModels = allWithLastPriceInfo.stream()
                .map(hotelDto -> HotelWithPriceInfoModel.builder()
                        .location(hotelDto.getLocation().getName())
                        .name(hotelDto.getName())
                        .lastModifiedDate(hotelDto.getLastModifiedDate())
                        .rating(hotelDto.getRating())
                        .lastPrice((Objects.nonNull(hotelDto.getPrices()) && !hotelDto.getPrices().isEmpty()) ?
                                hotelDto.getPrices().get(0).getCurrentPrice() : null)
                        .build()).collect(Collectors.toList());
        return HotelListResponse.builder()
                .hotels(hotelModels)
                .build();
    }
}
