package com.shameless.bookingtech.domain.repository;

import com.shameless.bookingtech.domain.entity.HotelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HotelRepository extends JpaRepository<HotelEntity, Long> {
    Optional<HotelEntity> findByName(String hotelName);
}
