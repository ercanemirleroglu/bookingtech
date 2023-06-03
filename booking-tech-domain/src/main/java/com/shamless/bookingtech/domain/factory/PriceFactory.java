package com.shamless.bookingtech.domain.factory;

import com.shamless.bookingtech.domain.dto.LocationDto;
import com.shamless.bookingtech.domain.dto.PriceDto;
import com.shamless.bookingtech.domain.entity.HotelEntity;
import com.shamless.bookingtech.domain.entity.LocationEntity;
import com.shamless.bookingtech.domain.entity.PriceEntity;
import com.shamless.bookingtech.domain.mapper.AppMoneyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PriceFactory {
    public PriceEntity from(PriceDto dto, HotelEntity hotel) {
        return PriceEntity.builder()
                .hotel(hotel)
                .currentPrice(AppMoneyMapper.INSTANCE.toMoney(dto.getCurrentPrice()))
                .previousPrice(AppMoneyMapper.INSTANCE.toMoney(dto.getPreviousPrice()))
                .paramAdult(dto.getParamAdult())
                .paramChild(dto.getParamChild())
                .paramRoom(dto.getParamRoom())
                .build();
    }
}
