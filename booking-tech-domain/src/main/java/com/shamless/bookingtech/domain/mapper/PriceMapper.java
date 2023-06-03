package com.shamless.bookingtech.domain.mapper;

import com.shamless.bookingtech.domain.dto.PriceDto;
import com.shamless.bookingtech.domain.entity.PriceEntity;

public class PriceMapper {
    public static final PriceMapper INSTANCE = new PriceMapper();

    public PriceDto toDto(PriceEntity entity) {
        return PriceDto.builder()
                .id(entity.getId())
                .createdDate(entity.getCreatedDate())
                .lastModifiedDate(entity.getLastModifiedDate())
                .currentPrice(AppMoneyMapper.INSTANCE.toDto(entity.getCurrentPrice()))
                .previousPrice(AppMoneyMapper.INSTANCE.toDto(entity.getPreviousPrice()))
                .hotel(HotelMapper.INSTANCE.toDto(entity.getHotel()))
                .paramAdult(entity.getParamAdult())
                .paramChild(entity.getParamChild())
                .paramRoom(entity.getParamRoom())
                .build();
    }
}
