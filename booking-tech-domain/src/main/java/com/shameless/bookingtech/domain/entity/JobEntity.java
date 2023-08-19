package com.shameless.bookingtech.domain.entity;

import org.apache.commons.lang3.StringUtils;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "job")
@Entity
@SequenceGenerator(name = "job_id_seq_generator", sequenceName = "job_id_seq", allocationSize = 1)
public class JobEntity extends AbstractEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_id_seq_generator")
    private Long id;
    @Enumerated(EnumType.STRING)
    private JobStatus status;
    private LocalDateTime lastTriggerDateTime;
    private LocalDateTime lastProcessDateTime;
    private String lastProcessAction;
    @Enumerated(EnumType.STRING)
    private TriggerType triggerType;
    private String name;
    private boolean permissionSync;
    private String error;

    public void start(TriggerType triggerType){
        LocalDateTime now = LocalDateTime.now();
        this.status = JobStatus.WORKING;
        this.lastTriggerDateTime = now;
        this.lastProcessDateTime = now;
        this.lastProcessAction = "Job has been started";
        this.triggerType = triggerType;
        this.error = null;
    }

    public void error(String error){
        LocalDateTime now = LocalDateTime.now();
        this.status = JobStatus.WAITING;
        this.lastProcessDateTime = now;
        this.lastProcessAction = "An error interrupt the job!";
        this.error = (StringUtils.isNotBlank(error) ?
                (error.length() > 255 ? error.substring(0, 255) : error)
                : "Undefined Error");
    }

    public void finish(){
        LocalDateTime now = LocalDateTime.now();
        this.status = JobStatus.WAITING;
        this.lastProcessDateTime = now;
        this.lastProcessAction = "Job is finished normally";
        this.error = null;
    }
}
