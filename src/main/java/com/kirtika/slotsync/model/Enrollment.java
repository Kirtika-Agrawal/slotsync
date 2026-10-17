package com.kirtika.slotsync.model;

import jakarta.persistence.*;

@Entity
@Table(name = "enrollments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "slot_id"}))
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id")
    private Slot slot;

    protected Enrollment() {
        // JPA needs an empty constructor
    }

    public Enrollment(User user, Course course, Slot slot) {
        this.user = user;
        this.course = course;
        this.slot = slot;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Course getCourse() { return course; }
    public Slot getSlot() { return slot; }
}