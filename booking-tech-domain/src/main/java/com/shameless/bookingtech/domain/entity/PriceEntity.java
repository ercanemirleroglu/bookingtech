package com.shameless.bookingtech.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "price")
@Entity
@SequenceGenerator(name = "price_id_seq_generator", sequenceName = "price_id_seq", allocationSize = 1)
public class PriceEntity extends AbstractEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "price_id_seq_generator")
    private Long id;
    private BigDecimal currentPrice;
    private String currentCurrency;
    private BigDecimal previousPrice;
    private String previousCurrency;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private HotelEntity hotel;

    @ManyToOne
    @JoinColumn(name = "search_criteria_id")
    private SearchCriteriaEntity searchCriteria;

}
