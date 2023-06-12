package com.shameless.bookingtech.domain.repository;

import com.shameless.bookingtech.domain.entity.HotelEntity;
import com.shameless.bookingtech.domain.entity.PriceEntity;
import com.shameless.bookingtech.domain.entity.SearchCriteriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PriceRepository extends JpaRepository<PriceEntity, Long> {
    Optional<PriceEntity> findFirstByHotelAndSearchCriteriaOrderByLastModifiedDateDesc(HotelEntity hotel, SearchCriteriaEntity searchCriteria);
}
