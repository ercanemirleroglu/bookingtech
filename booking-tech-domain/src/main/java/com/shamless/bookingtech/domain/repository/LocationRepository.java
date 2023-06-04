package com.shamless.bookingtech.domain.repository;

import com.shamless.bookingtech.domain.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
}
