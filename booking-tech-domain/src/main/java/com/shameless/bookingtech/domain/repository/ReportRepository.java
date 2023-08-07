package com.shameless.bookingtech.domain.repository;

import com.shameless.bookingtech.domain.entity.ReportEntity;
import com.shameless.bookingtech.domain.entity.StoreType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    boolean existsByDayAndReportType(LocalDate day, StoreType reportType);

    Optional<ReportEntity> findByDayAndReportType(LocalDate day, StoreType reportType);

    Optional<ReportEntity> findByReportType(StoreType reportType);
}
