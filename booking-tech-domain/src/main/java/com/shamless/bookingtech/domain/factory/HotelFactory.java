package com.shamless.bookingtech.domain.factory;

import com.shamless.bookingtech.domain.dto.HotelDto;
import com.shamless.bookingtech.domain.dto.LocationDto;
import com.shamless.bookingtech.domain.entity.HotelEntity;
import com.shamless.bookingtech.domain.entity.LocationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HotelFactory {
    public HotelEntity from(HotelDto dto, LocationEntity location) {
        return HotelEntity.builder()
                .name(dto.getName())
                .location(location)
                .rating(dto.getRating())
                .build();
    }
}
