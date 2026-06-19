package com.softuni.expensesplitter.service;

import com.softuni.expensesplitter.dto.CreateMemberRequest;
import com.softuni.expensesplitter.dto.MemberResponse;
import com.softuni.expensesplitter.entity.ExpenseGroup;
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
class MemberServiceTests {

    @Autowired
    private MemberService memberService;

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
    void addMemberToGroupAddsMemberToExistingGroup() {
        ExpenseGroup group = expenseGroupRepository.save(new ExpenseGroup("Trip", "Summer trip"));
        CreateMemberRequest request = createMemberRequest("Maria");

        MemberResponse response = memberService.addMemberToGroup(group.getId(), request);

        assertNotNull(response.getId());
        assertEquals("Maria", response.getName());
        assertEquals(group.getId(), response.getGroupId());
    }

    @Test
    void getMembersByGroupIdReturnsOnlyMembersForSelectedGroup() {
        ExpenseGroup firstGroup = expenseGroupRepository.save(new ExpenseGroup("Trip", "Summer trip"));
        ExpenseGroup secondGroup = expenseGroupRepository.save(new ExpenseGroup("Dinner", "Team dinner"));
        memberService.addMemberToGroup(firstGroup.getId(), createMemberRequest("Maria"));
        memberService.addMemberToGroup(secondGroup.getId(), createMemberRequest("Ivan"));

        List<MemberResponse> members = memberService.getMembersByGroupId(firstGroup.getId());

        assertEquals(1, members.size());
        assertEquals("Maria", members.get(0).getName());
        assertEquals(firstGroup.getId(), members.get(0).getGroupId());
    }

    @Test
    void addMemberToGroupThrowsNotFoundForMissingGroup() {
        CreateMemberRequest request = createMemberRequest("Maria");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> memberService.addMemberToGroup(999L, request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void getMembersByGroupIdThrowsNotFoundForMissingGroup() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> memberService.getMembersByGroupId(999L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void removeMemberFromGroupDeletesMemberFromSelectedGroup() {
        ExpenseGroup group = expenseGroupRepository.save(new ExpenseGroup("Trip", "Summer trip"));
        MemberResponse member = memberService.addMemberToGroup(group.getId(), createMemberRequest("Maria"));

        memberService.removeMemberFromGroup(group.getId(), member.getId());

        assertTrue(memberService.getMembersByGroupId(group.getId()).isEmpty());
        assertFalse(memberRepository.existsById(member.getId()));
    }

    @Test
    void removeMemberFromGroupThrowsNotFoundWhenMemberBelongsToAnotherGroup() {
        ExpenseGroup firstGroup = expenseGroupRepository.save(new ExpenseGroup("Trip", "Summer trip"));
        ExpenseGroup secondGroup = expenseGroupRepository.save(new ExpenseGroup("Dinner", "Team dinner"));
        MemberResponse member = memberService.addMemberToGroup(secondGroup.getId(), createMemberRequest("Ivan"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> memberService.removeMemberFromGroup(firstGroup.getId(), member.getId())
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(memberRepository.existsById(member.getId()));
    }

    @Test
    void removeMemberFromGroupThrowsNotFoundForMissingGroup() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> memberService.removeMemberFromGroup(999L, 1L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void removeMemberFromGroupThrowsNotFoundForMissingMember() {
        ExpenseGroup group = expenseGroupRepository.save(new ExpenseGroup("Trip", "Summer trip"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> memberService.removeMemberFromGroup(group.getId(), 999L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    private CreateMemberRequest createMemberRequest(String name) {
        CreateMemberRequest request = new CreateMemberRequest();
        request.setName(name);
        return request;
    }
}
