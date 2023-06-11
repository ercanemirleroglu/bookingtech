package com.shameless.bookingtech.domain.mapper;

import com.shameless.bookingtech.domain.dto.HotelDto;
import com.shameless.bookingtech.domain.entity.HotelEntity;

public class HotelMapper {
    public static final HotelMapper INSTANCE = new HotelMapper();

    public HotelDto toDto(HotelEntity entity) {
        return HotelDto.builder()
                .id(entity.getId())
                .createdDate(entity.getCreatedDate())
                .lastModifiedDate(entity.getLastModifiedDate())
                .name(entity.getName())
                .location(LocationMapper.INSTANCE.toDto(entity.getLocation()))
                .rating(entity.getRating())
                .build();
    }



}
