package com.shameless.bookingtech.domain.factory;

import com.shameless.bookingtech.domain.dto.HotelDto;
import com.shameless.bookingtech.domain.entity.HotelEntity;
import com.shameless.bookingtech.domain.entity.LocationEntity;
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
