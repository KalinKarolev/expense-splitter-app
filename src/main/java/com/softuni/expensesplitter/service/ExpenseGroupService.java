package com.softuni.expensesplitter.service;

import com.softuni.expensesplitter.dto.CreateExpenseGroupRequest;
import com.softuni.expensesplitter.dto.ExpenseGroupResponse;
import com.softuni.expensesplitter.entity.ExpenseGroup;
import com.softuni.expensesplitter.repository.ExpenseGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseGroupService {

    private final ExpenseGroupRepository repository;

    @Autowired
    public ExpenseGroupService(ExpenseGroupRepository repository) {
        this.repository = repository;
    }

    public List<ExpenseGroupResponse> getAllGroups() {
        return repository.findAll()
                .stream()
                .map(this::toView)
                .collect(Collectors.toList());
    }

    public ExpenseGroupResponse createGroup(CreateExpenseGroupRequest dto) {
        ExpenseGroup group = new ExpenseGroup(dto.getName(), dto.getDescription());
        ExpenseGroup saved = repository.save(group);
        return toView(saved);
    }

    public ExpenseGroupResponse getGroupById(Long id) {
        ExpenseGroup g = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
        return toView(g);
    }

    private ExpenseGroupResponse toView(ExpenseGroup g) {
        return new ExpenseGroupResponse(g.getId(), g.getName(), g.getDescription());
    }
}
