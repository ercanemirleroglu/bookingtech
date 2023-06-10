package com.shameless.bookingtech.domain.repository;

import com.shameless.bookingtech.domain.entity.SearchCriteriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SearchCriteriaRepository extends JpaRepository<SearchCriteriaEntity, Long> {

    Optional<SearchCriteriaEntity> findByParamAdultAndParamChildAndParamRoomAndLocation(Integer paramAdult, Integer paramChild, Integer paramRoom, String Location);
}
