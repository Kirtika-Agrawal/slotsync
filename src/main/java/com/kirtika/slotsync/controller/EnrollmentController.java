package com.kirtika.slotsync.controller;

import com.kirtika.slotsync.dto.EnrollmentRequest;
import com.kirtika.slotsync.dto.EnrollmentResponse;
import com.kirtika.slotsync.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<EnrollmentResponse> addCourse(@Valid @RequestBody EnrollmentRequest request,
                                              Authentication authentication) {
        return enrollmentService.addCourse(authentication.getName(), request);
    }

    @GetMapping
    public List<EnrollmentResponse> myEnrollments(Authentication authentication) {
        return enrollmentService.listForUser(authentication.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long id, Authentication authentication) {
        enrollmentService.remove(authentication.getName(), id);
    }
}