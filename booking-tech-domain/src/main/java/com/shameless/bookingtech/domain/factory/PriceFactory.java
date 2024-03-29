package com.shameless.bookingtech.domain.factory;

import com.shameless.bookingtech.domain.dto.PriceDto;
import com.shameless.bookingtech.domain.entity.HotelEntity;
import com.shameless.bookingtech.domain.entity.PriceEntity;
import com.shameless.bookingtech.domain.entity.SearchCriteriaEntity;
import com.shameless.bookingtech.domain.entity.StoreType;
import com.shameless.bookingtech.domain.mapper.AppMoneyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PriceFactory {
    public PriceEntity from(PriceDto dto, HotelEntity hotel, SearchCriteriaEntity searchCriteria) {
        return PriceEntity.builder()
                .hotel(hotel)
                .currentValue(dto.getCurrentPrice())
                .previousValue(dto.getPreviousPrice())
                .fromDate(dto.getFromDate())
                .toDate(dto.getToDate())
                .storeType(StoreType.valueOf(dto.getStoreType().name()))
                .searchCriteria(searchCriteria)
                .processDateTime(dto.getProcessDateTime())
                .build();
    }
}
