package com.softuni.expensesplitter.service;

import com.softuni.expensesplitter.dto.CreateExpenseRequest;
import com.softuni.expensesplitter.dto.ExpenseResponse;
import com.softuni.expensesplitter.entity.Expense;
import com.softuni.expensesplitter.entity.ExpenseGroup;
import com.softuni.expensesplitter.entity.Member;
import com.softuni.expensesplitter.repository.ExpenseGroupRepository;
import com.softuni.expensesplitter.repository.ExpenseRepository;
import com.softuni.expensesplitter.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseGroupRepository expenseGroupRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public ExpenseService(
            ExpenseRepository expenseRepository,
            ExpenseGroupRepository expenseGroupRepository,
            MemberRepository memberRepository) {
        this.expenseRepository = expenseRepository;
        this.expenseGroupRepository = expenseGroupRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public ExpenseResponse addExpenseToGroup(Long groupId, CreateExpenseRequest dto) {
        ExpenseGroup group = expenseGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));

        Member payer = memberRepository.findById(dto.getPayerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payer not found"));

        if (!payer.getExpenseGroup().getId().equals(groupId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payer does not belong to this group");
        }

        Expense expense = new Expense(dto.getDescription(), dto.getAmount(), group, payer);
        Expense saved = expenseRepository.save(expense);
        return toView(saved);
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpensesByGroupId(Long groupId) {
        if (!expenseGroupRepository.existsById(groupId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found");
        }

        return expenseRepository.findByExpenseGroupId(groupId)
                .stream()
                .map(this::toView)
                .collect(Collectors.toList());
    }

    private ExpenseResponse toView(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getExpenseGroup().getId(),
                expense.getPayer().getId(),
                expense.getPayer().getName()
        );
    }
}
