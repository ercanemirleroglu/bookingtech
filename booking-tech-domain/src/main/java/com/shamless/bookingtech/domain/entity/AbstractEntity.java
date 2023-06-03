package com.shamless.bookingtech.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class AbstractEntity<T> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2344931722588240596L;

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public abstract T getId();

}
