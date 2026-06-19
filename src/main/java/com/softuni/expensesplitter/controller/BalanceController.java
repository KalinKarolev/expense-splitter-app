package com.softuni.expensesplitter.controller;

import com.softuni.expensesplitter.dto.MemberBalanceResponse;
import com.softuni.expensesplitter.dto.SettlementResponse;
import com.softuni.expensesplitter.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{id}")
public class BalanceController {

    private final BalanceService balanceService;

    @Autowired
    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping("/balances")
    public List<MemberBalanceResponse> getBalancesByGroupId(@PathVariable("id") Long groupId) {
        return balanceService.getBalancesByGroupId(groupId);
    }

    @GetMapping("/settlements")
    public List<SettlementResponse> getSettlementsByGroupId(@PathVariable("id") Long groupId) {
        return balanceService.getSettlementsByGroupId(groupId);
    }
}
