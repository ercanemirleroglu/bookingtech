package com.shameless.bookingtech.domain.entity;

import com.shameless.bookingtech.domain.dto.ReportDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "report")
@Entity
@SequenceGenerator(name = "report_id_seq_generator", sequenceName = "report_id_seq", allocationSize = 1)
public class ReportEntity extends AbstractEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_id_seq_generator")
    private Long id;
    @Enumerated(EnumType.STRING)
    private StoreType reportType;
    private LocalDate day;
    private LocalDateTime lastReportDate;
    private LocalDate lastPriceDay;
    private LocalDate fromDate;
    private LocalDate toDate;

    public void update(ReportDto reportDto) {
        lastReportDate = reportDto.getLastReportDate();
        day = reportDto.getDay();
        lastPriceDay = reportDto.getLastPriceDay();
        fromDate = reportDto.getFromDate();
        toDate = reportDto.getToDate();
    }
}
