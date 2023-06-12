package com.shameless.bookingtech.domain.mapper;

import com.shameless.bookingtech.domain.dto.PriceDto;
import com.shameless.bookingtech.domain.entity.PriceEntity;

public class PriceMapper {
    public static final PriceMapper INSTANCE = new PriceMapper();

    public PriceDto toDto(PriceEntity entity) {
        return PriceDto.builder()
                .id(entity.getId())
                .createdDate(entity.getCreatedDate())
                .lastModifiedDate(entity.getLastModifiedDate())
                .currentPrice(entity.getCurrentValue())
                .currentCurrency(entity.getCurrentCurrency())
                .previousPrice(entity.getPreviousValue())
                .previousCurrency(entity.getPreviousCurrency())
                .hotel(HotelMapper.INSTANCE.toDto(entity.getHotel()))
                .searchCriteria(SearchCriteriaMapper.INSTANCE.toDto(entity.getSearchCriteria()))
                .build();
    }
}
