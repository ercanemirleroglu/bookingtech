package com.shameless.bookingtech.domain.service;

import com.shameless.bookingtech.domain.dto.ReportDto;
import com.shameless.bookingtech.domain.dto.StoreTypeDto;
import com.shameless.bookingtech.domain.entity.ReportEntity;
import com.shameless.bookingtech.domain.entity.StoreType;
import com.shameless.bookingtech.domain.factory.ReportFactory;
import com.shameless.bookingtech.domain.mapper.ReportMapper;
import com.shameless.bookingtech.domain.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportFactory reportFactory;

    public ReportService(ReportRepository reportRepository, ReportFactory reportFactory) {
        this.reportRepository = reportRepository;
        this.reportFactory = reportFactory;
    }

    public void createOrUpdateReport(ReportDto reportDto) {
        Optional<ReportEntity> byReportType = reportRepository.findByReportType(
                StoreType.valueOf(reportDto.getReportType().name()));
        if (byReportType.isPresent()) {
            ReportEntity report = byReportType.get();
            report.update(reportDto);
        } else {
            ReportEntity from = reportFactory.from(reportDto);
            reportRepository.save(from);
        }
    }

    public ReportDto getReportByType(StoreTypeDto reportType) {
        return reportRepository.findByReportType(StoreType.valueOf(reportType.name())).map(ReportMapper.INSTANCE::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Report not found: " + reportType.name()));
    }

    public Optional<ReportDto> getReportByTypeAndDate(StoreTypeDto reportType, LocalDate date) {
        return reportRepository.findByDayAndReportType(date, StoreType.valueOf(reportType.name()))
                .map(ReportMapper.INSTANCE::toDto);
    }
}
