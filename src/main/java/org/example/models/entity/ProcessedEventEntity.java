package org.example.models.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "processed_events")
public class ProcessedEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "events_seq")
    @SequenceGenerator(name = "events_seq", sequenceName = "events_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "request_id", nullable = false)
    private String requestId;

    @Column(name = "status", nullable = false, length = 15)
    private String status;

    public ProcessedEventEntity(String requestId, String status) {
        this.status = status;
        this.requestId = requestId;
    }

    public ProcessedEventEntity() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

