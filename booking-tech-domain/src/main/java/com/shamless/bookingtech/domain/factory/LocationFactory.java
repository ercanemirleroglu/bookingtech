package com.shamless.bookingtech.domain.factory;

import com.shamless.bookingtech.domain.dto.LocationDto;
import com.shamless.bookingtech.domain.entity.LocationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocationFactory {
    public LocationEntity from(LocationDto dto) {
        return LocationEntity.builder()
                .name(dto.getName())
                .build();
    }
}
