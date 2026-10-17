package com.kirtika.slotsync.controller;

import com.kirtika.slotsync.dto.CreateGroupRequest;
import com.kirtika.slotsync.dto.GroupResponse;
import com.kirtika.slotsync.dto.JoinGroupRequest;
import com.kirtika.slotsync.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupResponse create(@Valid @RequestBody CreateGroupRequest request,
                                Authentication authentication) {
        return groupService.create(authentication.getName(), request);
    }

    @PostMapping("/join")
    public GroupResponse join(@Valid @RequestBody JoinGroupRequest request,
                              Authentication authentication) {
        return groupService.join(authentication.getName(), request);
    }

    @GetMapping
    public List<GroupResponse> myGroups(Authentication authentication) {
        return groupService.listForUser(authentication.getName());
    }
}