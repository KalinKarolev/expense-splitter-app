package com.softuni.expensesplitter.controller;

import com.softuni.expensesplitter.dto.GroupCreateDTO;
import com.softuni.expensesplitter.dto.GroupViewDTO;
import com.softuni.expensesplitter.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public List<GroupViewDTO> getAll() {
        return groupService.getAllGroups();
    }

    @PostMapping
    public ResponseEntity<GroupViewDTO> create(@Valid @RequestBody GroupCreateDTO dto, UriComponentsBuilder uriBuilder) {
        GroupViewDTO created = groupService.createGroup(dto);
        URI location = uriBuilder.path("/api/groups/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public GroupViewDTO getOne(@PathVariable Long id) {
        return groupService.getGroupById(id);
    }
}
