package com.shameless.bookingtech.domain.repository;

import com.shameless.bookingtech.domain.entity.JobEntity;
import com.shameless.bookingtech.domain.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobRepository extends JpaRepository<JobEntity, Long> {

    Optional<JobEntity> findByName(String jobName);

    boolean existsByStatusAndIdNot(JobStatus status, Long id);
}
