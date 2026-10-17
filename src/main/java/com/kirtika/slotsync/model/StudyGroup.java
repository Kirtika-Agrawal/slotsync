package com.kirtika.slotsync.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "study_groups")
public class StudyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "invite_code", nullable = false, unique = true, length = 12)
    private String inviteCode;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected StudyGroup() {
        // JPA needs an empty constructor
    }

    public StudyGroup(String name, String inviteCode, User createdBy) {
        this.name = name;
        this.inviteCode = inviteCode;
        this.createdBy = createdBy;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getInviteCode() { return inviteCode; }
    public User getCreatedBy() { return createdBy; }
    public Instant getCreatedAt() { return createdAt; }
}