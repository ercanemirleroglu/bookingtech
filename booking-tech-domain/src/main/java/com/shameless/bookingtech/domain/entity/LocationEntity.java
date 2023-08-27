package com.shameless.bookingtech.domain.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.EqualsAndHashCode;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "location")
@Entity
@SequenceGenerator(name = "location_id_seq_generator", sequenceName = "location_id_seq", allocationSize = 1)
public class LocationEntity extends AbstractEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_id_seq_generator")
    private Long id;

    private String name;

    @OneToMany
    @JoinColumn(name = "location_id")
    private List<HotelEntity> hotels;

}
