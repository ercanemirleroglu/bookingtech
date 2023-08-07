package com.shameless.bookingtech.app.user;

import com.shameless.bookingtech.domain.entity.HotelEntity;
import com.shameless.bookingtech.domain.entity.CustomerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerFactory {
    private final PasswordEncoder passwordEncoder;

    public CustomerEntity from(CustomerDto dto, HotelEntity hotel) {
        return CustomerEntity.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getCredential()))
                .name(dto.getName())
                .surname(dto.getSurname())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .hotel(hotel)
                .build();
    }
}
