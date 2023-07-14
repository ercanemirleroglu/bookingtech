package com.shameless.bookingtech.domain.repository;

import com.shameless.bookingtech.domain.entity.ParamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ParamRepository extends JpaRepository<ParamEntity, Long> {
    @Query("select p " +
            "from ParamEntity p " +
            "where " +
            "p.paramKey like 'SEARCH%' " +
            "or " +
            "p.paramKey like '%CURRENCY%' ")
    List<ParamEntity> findSearchParams();

    @Query("select p " +
            "from ParamEntity p " +
            "where " +
            "p.paramKey like 'EMAIL_TO' ")
    Optional<ParamEntity> findEmailToParam();
}
