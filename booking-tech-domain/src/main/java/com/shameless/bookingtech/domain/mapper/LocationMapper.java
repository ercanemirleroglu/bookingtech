package com.shameless.bookingtech.domain.mapper;

import com.shameless.bookingtech.domain.dto.LocationDto;
import com.shameless.bookingtech.domain.entity.LocationEntity;

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
