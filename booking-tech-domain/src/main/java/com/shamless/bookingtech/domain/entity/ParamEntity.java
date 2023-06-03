package com.shamless.bookingtech.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "param")
@Entity
@SequenceGenerator(name = "param_id_seq_generator", sequenceName = "param_id_seq", allocationSize = 1)
public class ParamEntity extends AbstractEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "param_id_seq_generator")
    private Long id;
    private String key;
    private String value;

}
