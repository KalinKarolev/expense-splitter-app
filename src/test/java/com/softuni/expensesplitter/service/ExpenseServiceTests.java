package com.softuni.expensesplitter.service;

import com.softuni.expensesplitter.dto.CreateExpenseRequest;
import com.softuni.expensesplitter.dto.ExpenseResponse;
import com.softuni.expensesplitter.entity.ExpenseGroup;
import com.softuni.expensesplitter.entity.Member;
import com.softuni.expensesplitter.repository.ExpenseGroupRepository;
import com.softuni.expensesplitter.repository.ExpenseRepository;
import com.softuni.expensesplitter.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExpenseServiceTests {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private ExpenseGroupRepository expenseGroupRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @BeforeEach
    void setUp() {
        expenseRepository.deleteAll();
        memberRepository.deleteAll();
        expenseGroupRepository.deleteAll();
    }

    @Test
    void addExpenseToGroupAddsExpenseWithPayerFromSameGroup() {
        ExpenseGroup group = expenseGroupRepository.save(new ExpenseGroup("Trip", "Summer trip"));
        Member payer = memberRepository.save(new Member("Maria", group));
        CreateExpenseRequest request = createExpenseRequest("Dinner", new BigDecimal("42.50"), payer.getId());

        ExpenseResponse response = expenseService.addExpenseToGroup(group.getId(), request);

        assertNotNull(response.getId());
        assertEquals("Dinner", response.getDescription());
        assertEquals(new BigDecimal("42.50"), response.getAmount());
        assertEquals(group.getId(), response.getGroupId());
        assertEquals(payer.getId(), response.getPayerId());
        assertEquals("Maria", response.getPayerName());
    }

    @Test
    void getExpensesByGroupIdReturnsOnlyExpensesForSelectedGroup() {
        ExpenseGroup firstGroup = expenseGroupRepository.save(new ExpenseGroup("Trip", "Summer trip"));
        ExpenseGroup secondGroup = expenseGroupRepository.save(new ExpenseGroup("Dinner", "Team dinner"));
        Member firstPayer = memberRepository.save(new Member("Maria", firstGroup));
        Member secondPayer = memberRepository.save(new Member("Ivan", secondGroup));
        expenseService.addExpenseToGroup(firstGroup.getId(), createExpenseRequest("Hotel", new BigDecimal("120.00"), firstPayer.getId()));
        expenseService.addExpenseToGroup(secondGroup.getId(), createExpenseRequest("Pizza", new BigDecimal("30.00"), secondPayer.getId()));

        List<ExpenseResponse> expenses = expenseService.getExpensesByGroupId(firstGroup.getId());

        assertEquals(1, expenses.size());
        assertEquals("Hotel", expenses.get(0).getDescription());
        assertEquals(firstGroup.getId(), expenses.get(0).getGroupId());
        assertEquals(firstPayer.getId(), expenses.get(0).getPayerId());
    }

    @Test
    void addExpenseToGroupThrowsBadRequestWhenPayerBelongsToAnotherGroup() {
        ExpenseGroup firstGroup = expenseGroupRepository.save(new ExpenseGroup("Trip", "Summer trip"));
        ExpenseGroup secondGroup = expenseGroupRepository.save(new ExpenseGroup("Dinner", "Team dinner"));
        Member otherGroupPayer = memberRepository.save(new Member("Ivan", secondGroup));
        CreateExpenseRequest request = createExpenseRequest("Taxi", new BigDecimal("18.00"), otherGroupPayer.getId());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> expenseService.addExpenseToGroup(firstGroup.getId(), request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(expenseRepository.findByExpenseGroupId(firstGroup.getId()).isEmpty());
    }

    @Test
    void addExpenseToGroupThrowsNotFoundForMissingGroup() {
        CreateExpenseRequest request = createExpenseRequest("Taxi", new BigDecimal("18.00"), 1L);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> expenseService.addExpenseToGroup(999L, request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    private CreateExpenseRequest createExpenseRequest(String description, BigDecimal amount, Long payerId) {
        CreateExpenseRequest request = new CreateExpenseRequest();
        request.setDescription(description);
        request.setAmount(amount);
        request.setPayerId(payerId);
        return request;
    }
}
