package com.shameless.bookingtech.domain.repository;

import com.shameless.bookingtech.domain.entity.HotelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<HotelEntity, Long> {
    Optional<HotelEntity> findByName(String hotelName);

    @Query("select he from HotelEntity he join he.prices p join p.searchCriteria sc " +
            "where p.storeType = 'HOURLY' and sc.id = ?1 ")
    List<HotelEntity> findAllWithLastPriceInfo(Long scId);
}
