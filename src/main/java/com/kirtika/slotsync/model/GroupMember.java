package com.kirtika.slotsync.model;

import jakarta.persistence.*;

@Entity
@Table(name = "group_members",
        uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "user_id"}))
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private StudyGroup studyGroup;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    protected GroupMember() {
        // JPA needs an empty constructor
    }

    public GroupMember(StudyGroup studyGroup, User user) {
        this.studyGroup = studyGroup;
        this.user = user;
    }

    public Long getId() { return id; }
    public StudyGroup getStudyGroup() { return studyGroup; }
    public User getUser() { return user; }
}