package com.softuni.expensesplitter.controller;

import com.softuni.expensesplitter.dto.CreateMemberRequest;
import com.softuni.expensesplitter.dto.MemberResponse;
import com.softuni.expensesplitter.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/groups/{id}/members")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public List<MemberResponse> getMembersByGroupId(@PathVariable("id") Long groupId) {
        return memberService.getMembersByGroupId(groupId);
    }

    @PostMapping
    public ResponseEntity<MemberResponse> addMemberToGroup(
            @PathVariable("id") Long groupId,
            @Valid @RequestBody CreateMemberRequest dto,
            UriComponentsBuilder uriBuilder) {
        MemberResponse created = memberService.addMemberToGroup(groupId, dto);
        URI location = uriBuilder.path("/api/groups/{groupId}/members").buildAndExpand(groupId).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> removeMemberFromGroup(
            @PathVariable("id") Long groupId,
            @PathVariable Long memberId) {
        memberService.removeMemberFromGroup(groupId, memberId);
        return ResponseEntity.noContent().build();
    }
}
