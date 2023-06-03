package com.shamless.bookingtech.domain.entity;

import com.shamless.bookingtech.common.util.model.Param;
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
    @Enumerated(EnumType.STRING)
    private Param key;
    private String value;

}
