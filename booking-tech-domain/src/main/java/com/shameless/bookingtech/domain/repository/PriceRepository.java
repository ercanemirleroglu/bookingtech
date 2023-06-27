package com.shameless.bookingtech.domain.repository;

import com.shameless.bookingtech.domain.entity.HotelEntity;
import com.shameless.bookingtech.domain.entity.PriceEntity;
import com.shameless.bookingtech.domain.entity.SearchCriteriaEntity;
import com.shameless.bookingtech.domain.entity.StoreType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface PriceRepository extends JpaRepository<PriceEntity, Long> {
    Optional<PriceEntity> findFirstByHotelAndSearchCriteriaAndStoreTypeOrderByLastModifiedDateDesc(HotelEntity hotel, SearchCriteriaEntity searchCriteria, StoreType storeType);

    @Query("select p from PriceEntity p where p.hotel.id = ?1 and p.searchCriteria.id = ?2 " +
            " and p.storeType = 'PERIODIC' and p.fromDate = ?3 and p.toDate = ?4 ")
    Optional<PriceEntity> findByDateRange(Long hotelId, Long scId, LocalDate fromDate, LocalDate toDate);
}
