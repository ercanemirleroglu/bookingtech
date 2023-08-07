package com.shameless.bookingtech.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "customer")
@Entity
@SequenceGenerator(name = "customer_id_seq_generator", sequenceName = "customer_id_seq", allocationSize = 1)
public class CustomerEntity extends AbstractEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_id_seq_generator")
    private Long id;
    private String username;
    private String password;
    private String email;
    private String name;
    private String surname;
    private String phone;
    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private HotelEntity hotel;

}
