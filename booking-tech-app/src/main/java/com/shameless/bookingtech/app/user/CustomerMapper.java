package com.shameless.bookingtech.app.user;

import com.shameless.bookingtech.domain.entity.CustomerEntity;
import com.shameless.bookingtech.domain.mapper.HotelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerMapper {

    public CustomerDto toDto(CustomerEntity entity) {
        return CustomerDto.builder()
                .id(entity.getId())
                .createdDate(entity.getCreatedDate())
                .lastModifiedDate(entity.getLastModifiedDate())
                .username(entity.getUsername())
                .credential(entity.getPassword())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .name(entity.getName())
                .surname(entity.getSurname())
                .hotel(entity.getHotel() != null ? HotelMapper.INSTANCE.toDto(entity.getHotel()) : null)
                .build();
    }



}
