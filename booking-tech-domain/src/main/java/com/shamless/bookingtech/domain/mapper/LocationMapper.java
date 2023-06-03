package com.shamless.bookingtech.domain.mapper;

import com.shamless.bookingtech.domain.dto.LocationDto;
import com.shamless.bookingtech.domain.entity.LocationEntity;

public class LocationMapper {
    public static final LocationMapper INSTANCE = new LocationMapper();

    public LocationDto toDto(LocationEntity entity) {
        return LocationDto.builder()
                .id(entity.getId())
                .createdDate(entity.getCreatedDate())
                .lastModifiedDate(entity.getLastModifiedDate())
                .name(entity.getName())
                .build();
    }



}
