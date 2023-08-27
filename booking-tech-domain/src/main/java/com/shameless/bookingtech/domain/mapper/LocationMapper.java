package com.shameless.bookingtech.domain.mapper;

import com.shameless.bookingtech.domain.dto.LocationDto;
import com.shameless.bookingtech.domain.entity.LocationEntity;

import java.util.Objects;
import java.util.stream.Collectors;

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

    public LocationDto toDtoWithStatistics(LocationEntity entity) {
        LocationDto locationDto = toDto(entity);
        locationDto.setHotels((Objects.nonNull(entity.getHotels()) && !entity.getHotels().isEmpty()) ?
                entity.getHotels().stream().map(HotelMapper.INSTANCE::toDtoWithPrices).collect(Collectors.toList()) : null);
        return locationDto;
    }



}
