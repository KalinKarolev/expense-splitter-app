package com.softuni.expensesplitter.service;

import com.softuni.expensesplitter.dto.GroupCreateDTO;
import com.softuni.expensesplitter.dto.GroupViewDTO;
import com.softuni.expensesplitter.entity.ExpenseGroup;
import com.softuni.expensesplitter.repository.ExpenseGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final ExpenseGroupRepository repository;

    @Autowired
    public GroupService(ExpenseGroupRepository repository) {
        this.repository = repository;
    }

    public List<GroupViewDTO> getAllGroups() {
        return repository.findAll()
                .stream()
                .map(this::toView)
                .collect(Collectors.toList());
    }

    public GroupViewDTO createGroup(GroupCreateDTO dto) {
        ExpenseGroup group = new ExpenseGroup(dto.getName(), dto.getDescription());
        ExpenseGroup saved = repository.save(group);
        return toView(saved);
    }

    public GroupViewDTO getGroupById(Long id) {
        ExpenseGroup g = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
        return toView(g);
    }

    private GroupViewDTO toView(ExpenseGroup g) {
        return new GroupViewDTO(g.getId(), g.getName(), g.getDescription());
    }
}
