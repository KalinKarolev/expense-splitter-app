package com.softuni.expensesplitter.controller;

import com.softuni.expensesplitter.dto.CreateExpenseGroupRequest;
import com.softuni.expensesplitter.dto.ExpenseGroupResponse;
import com.softuni.expensesplitter.dto.UpdateExpenseGroupRequest;
import com.softuni.expensesplitter.service.ExpenseGroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class ExpenseGroupController {

    private final ExpenseGroupService expenseGroupService;

    @Autowired
    public ExpenseGroupController(ExpenseGroupService expenseGroupService) {
        this.expenseGroupService = expenseGroupService;
    }

    @GetMapping
    public List<ExpenseGroupResponse> getAllGroups() {
        return expenseGroupService.getAllGroups();
    }

    @PostMapping
    public ResponseEntity<ExpenseGroupResponse> createGroup(@Valid @RequestBody CreateExpenseGroupRequest dto, UriComponentsBuilder uriBuilder) {
        ExpenseGroupResponse created = expenseGroupService.createGroup(dto);
        URI location = uriBuilder.path("/api/groups/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ExpenseGroupResponse getGroupById(@PathVariable Long id) {
        return expenseGroupService.getGroupById(id);
    }

    @PatchMapping("/{id}")
    public ExpenseGroupResponse updateGroup(@PathVariable Long id, @RequestBody UpdateExpenseGroupRequest dto) {
        return expenseGroupService.updateGroup(id, dto);
    }
}
