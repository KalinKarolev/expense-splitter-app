package com.softuni.expensesplitter.service;

import com.softuni.expensesplitter.dto.CreateMemberRequest;
import com.softuni.expensesplitter.dto.MemberResponse;
import com.softuni.expensesplitter.entity.ExpenseGroup;
import com.softuni.expensesplitter.entity.Member;
import com.softuni.expensesplitter.repository.ExpenseGroupRepository;
import com.softuni.expensesplitter.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final ExpenseGroupRepository expenseGroupRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository, ExpenseGroupRepository expenseGroupRepository) {
        this.memberRepository = memberRepository;
        this.expenseGroupRepository = expenseGroupRepository;
    }

    @Transactional
    public MemberResponse addMemberToGroup(Long groupId, CreateMemberRequest dto) {
        ExpenseGroup group = expenseGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));

        Member member = new Member(dto.getName(), group);
        Member saved = memberRepository.save(member);
        return toView(saved);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getMembersByGroupId(Long groupId) {
        if (!expenseGroupRepository.existsById(groupId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found");
        }

        return memberRepository.findByExpenseGroupId(groupId)
                .stream()
                .map(this::toView)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeMemberFromGroup(Long groupId, Long memberId) {
        if (!expenseGroupRepository.existsById(groupId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));

        if (!member.getExpenseGroup().getId().equals(groupId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found");
        }

        memberRepository.delete(member);
    }

    private MemberResponse toView(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getExpenseGroup().getId()
        );
    }
}
