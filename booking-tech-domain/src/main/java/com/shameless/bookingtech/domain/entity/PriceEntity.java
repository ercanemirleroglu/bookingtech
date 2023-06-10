package com.shameless.bookingtech.domain.entity;

import com.shameless.bookingtech.domain.model.MoneyConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Columns;

import javax.money.MonetaryAmount;

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

    @Columns(columns = {
            @Column(name = "current_price_currency"),
            @Column(name = "current_price")
    })
    @Convert(converter = MoneyConverter.class)
    private MonetaryAmount currentPrice;

    @Columns(columns = {
            @Column(name = "previous_price_currency"),
            @Column(name = "previous_price")
    })
    @Convert(converter = MoneyConverter.class)
    private MonetaryAmount previousPrice;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private HotelEntity hotel;

    @ManyToOne
    @JoinColumn(name = "search_criteria_id")
    private SearchCriteriaEntity searchCriteria;



}
