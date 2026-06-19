package com.softuni.expensesplitter.controller;

import com.softuni.expensesplitter.dto.CreateExpenseRequest;
import com.softuni.expensesplitter.dto.ExpenseResponse;
import com.softuni.expensesplitter.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/groups/{id}/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public List<ExpenseResponse> getExpensesByGroupId(@PathVariable("id") Long groupId) {
        return expenseService.getExpensesByGroupId(groupId);
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> addExpenseToGroup(
            @PathVariable("id") Long groupId,
            @Valid @RequestBody CreateExpenseRequest dto,
            UriComponentsBuilder uriBuilder) {
        ExpenseResponse created = expenseService.addExpenseToGroup(groupId, dto);
        URI location = uriBuilder
                .path("/api/groups/{groupId}/expenses/{expenseId}")
                .buildAndExpand(groupId, created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }
}
