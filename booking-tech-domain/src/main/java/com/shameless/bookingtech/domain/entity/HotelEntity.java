package com.shameless.bookingtech.domain.entity;

import com.shameless.bookingtech.domain.dto.HotelDto;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "hotel")
@Entity
@SequenceGenerator(name = "hotel_id_seq_generator", sequenceName = "hotel_id_seq", allocationSize = 1)
public class HotelEntity extends AbstractEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hotel_id_seq_generator")
    private Long id;

    private String name;

    private Double rating;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private LocationEntity location;

    public void update(HotelDto hotelDto) {
        this.rating = hotelDto.getRating();
    }

    public void update(LocationEntity location) {
        this.location = location;
    }
}
