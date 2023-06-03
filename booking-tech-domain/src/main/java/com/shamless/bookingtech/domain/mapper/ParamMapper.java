package com.shamless.bookingtech.domain.mapper;

import com.shamless.bookingtech.domain.dto.ParamDto;
import com.shamless.bookingtech.domain.entity.ParamEntity;

public class ParamMapper {
    public static final ParamMapper INSTANCE = new ParamMapper();

    public ParamDto toDto(ParamEntity entity) {
        return ParamDto.builder()
                .id(entity.getId())
                .createdDate(entity.getCreatedDate())
                .lastModifiedDate(entity.getLastModifiedDate())
                .key(entity.getKey())
                .value(entity.getValue())
                .build();
    }



}
