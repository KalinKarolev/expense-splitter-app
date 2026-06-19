package com.softuni.expensesplitter.service;

import com.softuni.expensesplitter.dto.CreateExpenseGroupRequest;
import com.softuni.expensesplitter.dto.ExpenseGroupResponse;
import com.softuni.expensesplitter.dto.UpdateExpenseGroupRequest;
import com.softuni.expensesplitter.repository.ExpenseGroupRepository;
import com.softuni.expensesplitter.repository.ExpenseRepository;
import com.softuni.expensesplitter.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExpenseGroupServiceTests {

    @Autowired
    private ExpenseGroupService expenseGroupService;

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
    void createGroupSavesGroupAndReturnsResponse() {
        CreateExpenseGroupRequest request = createGroupRequest("Trip", "Summer trip");

        ExpenseGroupResponse response = expenseGroupService.createGroup(request);

        assertNotNull(response.getId());
        assertEquals("Trip", response.getName());
        assertEquals("Summer trip", response.getDescription());
        assertTrue(expenseGroupRepository.existsById(response.getId()));
    }

    @Test
    void getAllGroupsReturnsCreatedGroups() {
        ExpenseGroupResponse firstGroup = expenseGroupService.createGroup(createGroupRequest("Trip", "Summer trip"));
        ExpenseGroupResponse secondGroup = expenseGroupService.createGroup(createGroupRequest("Dinner", "Team dinner"));

        List<ExpenseGroupResponse> groups = expenseGroupService.getAllGroups();

        assertEquals(2, groups.size());
        assertTrue(groups.stream().anyMatch(group -> group.getId().equals(firstGroup.getId())
                && group.getName().equals("Trip")
                && group.getDescription().equals("Summer trip")));
        assertTrue(groups.stream().anyMatch(group -> group.getId().equals(secondGroup.getId())
                && group.getName().equals("Dinner")
                && group.getDescription().equals("Team dinner")));
    }

    @Test
    void getGroupByIdReturnsSelectedGroup() {
        ExpenseGroupResponse created = expenseGroupService.createGroup(createGroupRequest("Trip", "Summer trip"));

        ExpenseGroupResponse response = expenseGroupService.getGroupById(created.getId());

        assertEquals(created.getId(), response.getId());
        assertEquals("Trip", response.getName());
        assertEquals("Summer trip", response.getDescription());
    }

    @Test
    void getGroupByIdThrowsNotFoundForMissingGroup() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> expenseGroupService.getGroupById(999L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void updateGroupChangesDescription() {
        ExpenseGroupResponse created = expenseGroupService.createGroup(createGroupRequest("Trip", "Summer trip"));
        UpdateExpenseGroupRequest request = updateGroupRequest("Updated trip description");

        ExpenseGroupResponse response = expenseGroupService.updateGroup(created.getId(), request);

        assertEquals(created.getId(), response.getId());
        assertEquals("Trip", response.getName());
        assertEquals("Updated trip description", response.getDescription());
        assertEquals("Updated trip description", expenseGroupService.getGroupById(created.getId()).getDescription());
    }

    @Test
    void updateGroupThrowsNotFoundForMissingGroup() {
        UpdateExpenseGroupRequest request = updateGroupRequest("Updated trip description");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> expenseGroupService.updateGroup(999L, request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    private CreateExpenseGroupRequest createGroupRequest(String name, String description) {
        CreateExpenseGroupRequest request = new CreateExpenseGroupRequest();
        request.setName(name);
        request.setDescription(description);
        return request;
    }

    private UpdateExpenseGroupRequest updateGroupRequest(String description) {
        UpdateExpenseGroupRequest request = new UpdateExpenseGroupRequest();
        request.setDescription(description);
        return request;
    }
}
