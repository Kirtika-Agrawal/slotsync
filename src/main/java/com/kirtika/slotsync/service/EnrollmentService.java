package com.kirtika.slotsync.service;

import com.kirtika.slotsync.dto.EnrollmentRequest;
import com.kirtika.slotsync.dto.EnrollmentResponse;
import com.kirtika.slotsync.model.Course;
import com.kirtika.slotsync.model.Enrollment;
import com.kirtika.slotsync.model.Slot;
import com.kirtika.slotsync.model.User;
import com.kirtika.slotsync.repository.CourseRepository;
import com.kirtika.slotsync.repository.EnrollmentRepository;
import com.kirtika.slotsync.repository.SlotRepository;
import com.kirtika.slotsync.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class EnrollmentService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final SlotRepository slotRepository;
    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentService(UserRepository userRepository,
                             CourseRepository courseRepository,
                             SlotRepository slotRepository,
                             EnrollmentRepository enrollmentRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.slotRepository = slotRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional
    public List<EnrollmentResponse> addCourse(String email, EnrollmentRequest request) {
        User user = getUser(email);

        String code = request.courseCode().trim().toUpperCase();
        Course course = courseRepository.findByCode(code)
                .orElseGet(() -> courseRepository.save(
                        new Course(code, request.courseTitle().trim())));

        List<Enrollment> created = new ArrayList<>();
        for (String rawSlot : request.slots()) {
            String slotName = rawSlot.trim().toUpperCase();

            Slot slot = slotRepository.findByName(slotName)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Unknown slot: " + slotName));

            if (enrollmentRepository.existsByUserAndSlot(user, slot)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "You already have a course in slot " + slotName);
            }

            created.add(enrollmentRepository.save(new Enrollment(user, course, slot)));
        }

        return created.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> listForUser(String email) {
        User user = getUser(email);
        return enrollmentRepository.findByUserOrderByIdAsc(user).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void remove(String email, Long enrollmentId) {
        User user = getUser(email);
        Enrollment enrollment = enrollmentRepository.findByIdAndUser(enrollmentId, user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Enrollment not found"));
        enrollmentRepository.delete(enrollment);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private EnrollmentResponse toResponse(Enrollment e) {
        return new EnrollmentResponse(
                e.getId(),
                e.getCourse().getCode(),
                e.getCourse().getTitle(),
                e.getSlot().getName());
    }
}