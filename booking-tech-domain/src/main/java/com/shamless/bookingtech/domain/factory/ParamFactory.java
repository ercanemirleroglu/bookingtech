package com.shamless.bookingtech.domain.factory;

import com.shamless.bookingtech.domain.dto.ParamDto;
import com.shamless.bookingtech.domain.entity.ParamEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParamFactory {
    public ParamEntity from(ParamDto dto) {
        return ParamEntity.builder()
                .key(dto.getKey())
                .value(dto.getValue())
                .build();
    }
}
