package com.smartcampus.core.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User recipient;

    @Column(nullable = false)
    private String message;

    private boolean readStatus;

    private LocalDateTime createdAt;

    public Notification() {}

    public Notification(Long id, User recipient, String message, boolean readStatus, LocalDateTime createdAt) {
        this.id = id;
        this.recipient = recipient;
        this.message = message;
        this.readStatus = readStatus;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public User getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public boolean isReadStatus() { return readStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setRecipient(User recipient) { this.recipient = recipient; }
    public void setMessage(String message) { this.message = message; }
    public void setReadStatus(boolean readStatus) { this.readStatus = readStatus; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private User recipient;
        private String message;
        private boolean readStatus;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder recipient(User recipient) { this.recipient = recipient; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder readStatus(boolean readStatus) { this.readStatus = readStatus; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Notification build() {
            return new Notification(id, recipient, message, readStatus, createdAt);
        }
    }
}
