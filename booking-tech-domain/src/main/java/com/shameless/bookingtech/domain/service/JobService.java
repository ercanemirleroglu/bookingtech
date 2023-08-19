package com.shameless.bookingtech.domain.service;

import com.shameless.bookingtech.common.exception.JobNotStartedException;
import com.shameless.bookingtech.domain.dto.JobDto;
import com.shameless.bookingtech.domain.dto.TriggerTypeDto;
import com.shameless.bookingtech.domain.entity.JobEntity;
import com.shameless.bookingtech.domain.entity.JobStatus;
import com.shameless.bookingtech.domain.entity.TriggerType;
import com.shameless.bookingtech.domain.factory.JobFactory;
import com.shameless.bookingtech.domain.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {
    private final JobRepository jobRepository;
    private final JobFactory jobFactory;

    private JobEntity findOrCreateJob(String jobName) {
        Optional<JobEntity> byName = jobRepository.findByName(jobName);
        JobDto.JobDtoBuilder jobDtoBuilder = JobDto.builder();
        if (byName.isEmpty()) {
            jobDtoBuilder
                    .name(jobName)
                    .permissionSync(false)
                    .lastProcessDateTime(LocalDateTime.now())
                    .lastProcessAction("Job is created");
            JobEntity from = jobFactory.from(jobDtoBuilder.build());
            byName = Optional.of(jobRepository.save(from));
        }
        return byName.get();
    }

    @Transactional
    public void start(String jobName, TriggerTypeDto triggerType) {
        JobEntity jobEntity = findOrCreateJob(jobName);
        if (!jobEntity.isPermissionSync() && (JobStatus.WORKING.equals(jobEntity.getStatus()) ||
                jobRepository.existsByStatusAndIdNot(JobStatus.WORKING, jobEntity.getId()))) {
            log.error(jobName + " or a different job is already working!");
            throw new JobNotStartedException(jobName + " or a different job is already working!");
        }
        jobEntity.start(TriggerType.valueOf(triggerType.name()));
        jobRepository.save(jobEntity);
    }

    public void error(String jobName, String error) {
        JobEntity jobEntity = jobRepository.findByName(jobName)
                .orElseThrow(() -> new IllegalArgumentException(jobName + " not found!"));
        jobEntity.error(error);
        jobRepository.save(jobEntity);
    }

    public void finish(String jobName) {
        JobEntity jobEntity = jobRepository.findByName(jobName)
                .orElseThrow(() -> new IllegalArgumentException(jobName + " not found!"));
        jobEntity.finish();
        jobRepository.save(jobEntity);
    }
}
