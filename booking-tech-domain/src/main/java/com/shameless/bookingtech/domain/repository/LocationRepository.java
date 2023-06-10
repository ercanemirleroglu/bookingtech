package com.shameless.bookingtech.domain.repository;

import com.shameless.bookingtech.domain.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
    boolean existsByName(String location);

    Optional<LocationEntity> findByName(String location);
}
