package com.shameless.bookingtech.domain.entity;

import com.shameless.bookingtech.domain.dto.SearchCriteriaDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "search_criteria")
@Entity
@SequenceGenerator(name = "search_criteria_id_seq_generator", sequenceName = "search_criteria_id_seq", allocationSize = 1)
public class SearchCriteriaEntity extends AbstractEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "search_criteria_id_seq_generator")
    private Long id;
    private Integer paramAdult;
    private Integer paramChild;
    private Integer paramRoom;
    private String paramLocation;
    private String paramCurrency;
    private LocalDate fromDate;
    private LocalDate toDate;

    public void update(SearchCriteriaDto searchCriteriaDto) {
        this.fromDate = searchCriteriaDto.getFromDate();
        this.toDate = searchCriteriaDto.getToDate();
    }
}
