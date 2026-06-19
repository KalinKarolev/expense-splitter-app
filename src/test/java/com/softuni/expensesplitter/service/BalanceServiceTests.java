package com.softuni.expensesplitter.service;

import com.softuni.expensesplitter.dto.MemberBalanceResponse;
import com.softuni.expensesplitter.dto.SettlementResponse;
import com.softuni.expensesplitter.entity.Expense;
import com.softuni.expensesplitter.entity.ExpenseGroup;
import com.softuni.expensesplitter.entity.Member;
import com.softuni.expensesplitter.repository.ExpenseGroupRepository;
import com.softuni.expensesplitter.repository.ExpenseRepository;
import com.softuni.expensesplitter.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BalanceServiceTests {

    @Autowired
    private BalanceService balanceService;

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
    void getBalancesByGroupIdReturnsEmptyListWhenGroupHasNoMembersAndNoExpenses() {
        ExpenseGroup group = expenseGroupRepository.save(new ExpenseGroup("Trip", "Summer trip"));

        List<MemberBalanceResponse> balances = balanceService.getBalancesByGroupId(group.getId());
        List<SettlementResponse> settlements = balanceService.getSettlementsByGroupId(group.getId());

        assertTrue(balances.isEmpty());
        assertTrue(settlements.isEmpty());
    }

    @Test
    void getBalancesByGroupIdReturnsZeroBalancesWhenMembersExistButNoExpenses() {
        ExpenseGroup group = expenseGroupRepository.save(new ExpenseGroup("Trip", "Summer trip"));
        Member maria = memberRepository.save(new Member("Maria", group));
        Member ivan = memberRepository.save(new Member("Ivan", group));

        List<MemberBalanceResponse> balances = balanceService.getBalancesByGroupId(group.getId());

        assertEquals(2, balances.size());
        assertBalance(balancesByMemberId(balances).get(maria.getId()), maria, "0.00", "0.00", "0.00");
        assertBalance(balancesByMemberId(balances).get(ivan.getId()), ivan, "0.00", "0.00", "0.00");
        assertTrue(balanceService.getSettlementsByGroupId(group.getId()).isEmpty());
    }

    @Test
    void getBalancesByGroupIdSplitsOneMemberPaidExpenseBetweenAllMembers() {
        ExpenseGroup group = expenseGroupRepository.save(new ExpenseGroup("Trip", "Summer trip"));
        Member maria = memberRepository.save(new Member("Maria", group));
        Member ivan = memberRepository.save(new Member("Ivan", group));
        Member petar = memberRepository.save(new Member("Petar", group));
        expenseRepository.save(new Expense("Hotel", new BigDecimal("90.00"), group, maria));

        Map<Long, MemberBalanceResponse> balances = balancesByMemberId(balanceService.getBalancesByGroupId(group.getId()));

        assertBalance(balances.get(maria.getId()), maria, "90.00", "30.00", "60.00");
        assertBalance(balances.get(ivan.getId()), ivan, "0.00", "30.00", "-30.00");
        assertBalance(balances.get(petar.getId()), petar, "0.00", "30.00", "-30.00");
    }

    @Test
    void getBalancesByGroupIdCalculatesMultipleMembersPaidDifferentAmounts() {
        ExpenseGroup group = expenseGroupRepository.save(new ExpenseGroup("Dinner", "Team dinner"));
        Member maria = memberRepository.save(new Member("Maria", group));
        Member ivan = memberRepository.save(new Member("Ivan", group));
        Member petar = memberRepository.save(new Member("Petar", group));
        expenseRepository.save(new Expense("Dinner", new BigDecimal("90.00"), group, maria));
        expenseRepository.save(new Expense("Taxi", new BigDecimal("30.00"), group, ivan));

        Map<Long, MemberBalanceResponse> balances = balancesByMemberId(balanceService.getBalancesByGroupId(group.getId()));

        assertBalance(balances.get(maria.getId()), maria, "90.00", "40.00", "50.00");
        assertBalance(balances.get(ivan.getId()), ivan, "30.00", "40.00", "-10.00");
        assertBalance(balances.get(petar.getId()), petar, "0.00", "40.00", "-40.00");
    }

    @Test
    void getSettlementsByGroupIdCreatesPaymentsThatSettleBalances() {
        ExpenseGroup group = expenseGroupRepository.save(new ExpenseGroup("Dinner", "Team dinner"));
        Member maria = memberRepository.save(new Member("Maria", group));
        Member ivan = memberRepository.save(new Member("Ivan", group));
        Member petar = memberRepository.save(new Member("Petar", group));
        expenseRepository.save(new Expense("Dinner", new BigDecimal("90.00"), group, maria));
        expenseRepository.save(new Expense("Taxi", new BigDecimal("30.00"), group, ivan));

        List<SettlementResponse> settlements = balanceService.getSettlementsByGroupId(group.getId());

        assertEquals(2, settlements.size());
        assertSettlement(settlements.get(0), ivan, maria, "10.00");
        assertSettlement(settlements.get(1), petar, maria, "40.00");
    }

    private Map<Long, MemberBalanceResponse> balancesByMemberId(List<MemberBalanceResponse> balances) {
        return balances.stream()
                .collect(Collectors.toMap(MemberBalanceResponse::getMemberId, balance -> balance));
    }

    private void assertBalance(
            MemberBalanceResponse balance,
            Member member,
            String paidAmount,
            String equalShare,
            String finalBalance) {
        assertNotNull(balance);
        assertEquals(member.getId(), balance.getMemberId());
        assertEquals(member.getName(), balance.getMemberName());
        assertEquals(new BigDecimal(paidAmount), balance.getPaidAmount());
        assertEquals(new BigDecimal(equalShare), balance.getEqualShare());
        assertEquals(new BigDecimal(finalBalance), balance.getBalance());
    }

    private void assertSettlement(SettlementResponse settlement, Member fromMember, Member toMember, String amount) {
        assertEquals(fromMember.getId(), settlement.getFromMemberId());
        assertEquals(fromMember.getName(), settlement.getFromMemberName());
        assertEquals(toMember.getId(), settlement.getToMemberId());
        assertEquals(toMember.getName(), settlement.getToMemberName());
        assertEquals(new BigDecimal(amount), settlement.getAmount());
    }
}
