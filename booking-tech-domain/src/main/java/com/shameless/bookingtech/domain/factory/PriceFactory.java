package com.shameless.bookingtech.domain.factory;

import com.shameless.bookingtech.domain.dto.PriceDto;
import com.shameless.bookingtech.domain.entity.HotelEntity;
import com.shameless.bookingtech.domain.entity.PriceEntity;
import com.shameless.bookingtech.domain.entity.SearchCriteriaEntity;
import com.shameless.bookingtech.domain.mapper.AppMoneyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PriceFactory {
    public PriceEntity from(PriceDto dto, HotelEntity hotel, SearchCriteriaEntity searchCriteria) {
        return PriceEntity.builder()
                .hotel(hotel)
                .currentPrice(dto.getCurrentPrice())
                .currentCurrency(dto.getCurrentCurrency())
                .previousPrice(dto.getPreviousPrice())
                .previousCurrency(dto.getPreviousCurrency())
                .searchCriteria(searchCriteria)
                .build();
    }
}
