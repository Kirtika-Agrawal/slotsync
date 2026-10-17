package com.kirtika.slotsync.controller;

import com.kirtika.slotsync.dto.SignupRequest;
import com.kirtika.slotsync.dto.UserResponse;
import com.kirtika.slotsync.model.User;
import com.kirtika.slotsync.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse signup(@Valid @RequestBody SignupRequest request) {
        User user = userService.signup(request);
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}