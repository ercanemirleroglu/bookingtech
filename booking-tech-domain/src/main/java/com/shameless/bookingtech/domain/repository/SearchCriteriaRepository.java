package com.shameless.bookingtech.domain.repository;

import com.shameless.bookingtech.domain.entity.SearchCriteriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SearchCriteriaRepository extends JpaRepository<SearchCriteriaEntity, Long> {

    @Query("select sc from SearchCriteriaEntity sc " +
            "where " +
            "sc.dayRange = ?1 and " +
            "sc.paramAdult = ?2 and " +
            "sc.paramChild = ?3 and " +
            "sc.paramRoom = ?4 and " +
            "sc.paramLocation = ?5 and " +
            "sc.paramCurrency = ?6 ")
    Optional<SearchCriteriaEntity> findBySearchParams(Integer dayRange, Integer paramAdult,
                                                      Integer paramChild, Integer paramRoom,
                                                      String location, String currency);
}
