package com.shameless.bookingtech.domain.repository;

import com.shameless.bookingtech.domain.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<PriceEntity, Long> {
}
