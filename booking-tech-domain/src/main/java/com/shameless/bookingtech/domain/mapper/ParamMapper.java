package com.shameless.bookingtech.domain.mapper;

import com.shameless.bookingtech.domain.dto.ParamDto;
import com.shameless.bookingtech.domain.entity.ParamEntity;

public class ParamMapper {
    public static final ParamMapper INSTANCE = new ParamMapper();

    public ParamDto toDto(ParamEntity entity) {
        return ParamDto.builder()
                .id(entity.getId())
                .createdDate(entity.getCreatedDate())
                .lastModifiedDate(entity.getLastModifiedDate())
                .key(entity.getParamKey())
                .value(entity.getParamValue())
                .build();
    }



}
