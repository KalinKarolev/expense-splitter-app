package com.softuni.expensesplitter.service;

import com.softuni.expensesplitter.dto.MemberBalanceResponse;
import com.softuni.expensesplitter.dto.SettlementResponse;
import com.softuni.expensesplitter.entity.Expense;
import com.softuni.expensesplitter.entity.Member;
import com.softuni.expensesplitter.repository.ExpenseGroupRepository;
import com.softuni.expensesplitter.repository.ExpenseRepository;
import com.softuni.expensesplitter.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BalanceService {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    private final ExpenseGroupRepository expenseGroupRepository;
    private final MemberRepository memberRepository;
    private final ExpenseRepository expenseRepository;

    @Autowired
    public BalanceService(
            ExpenseGroupRepository expenseGroupRepository,
            MemberRepository memberRepository,
            ExpenseRepository expenseRepository) {
        this.expenseGroupRepository = expenseGroupRepository;
        this.memberRepository = memberRepository;
        this.expenseRepository = expenseRepository;
    }

    @Transactional(readOnly = true)
    public List<MemberBalanceResponse> getBalancesByGroupId(Long groupId) {
        validateGroupExists(groupId);
        return calculateBalances(groupId);
    }

    @Transactional(readOnly = true)
    public List<SettlementResponse> getSettlementsByGroupId(Long groupId) {
        validateGroupExists(groupId);
        List<MemberBalanceResponse> balances = calculateBalances(groupId);
        List<SettlementParticipant> debtors = new ArrayList<>();
        List<SettlementParticipant> creditors = new ArrayList<>();

        for (MemberBalanceResponse balance : balances) {
            if (balance.getBalance().compareTo(ZERO) < 0) {
                debtors.add(new SettlementParticipant(
                        balance.getMemberId(),
                        balance.getMemberName(),
                        balance.getBalance().abs()
                ));
            } else if (balance.getBalance().compareTo(ZERO) > 0) {
                creditors.add(new SettlementParticipant(
                        balance.getMemberId(),
                        balance.getMemberName(),
                        balance.getBalance()
                ));
            }
        }

        List<SettlementResponse> settlements = new ArrayList<>();
        int debtorIndex = 0;
        int creditorIndex = 0;

        while (debtorIndex < debtors.size() && creditorIndex < creditors.size()) {
            SettlementParticipant debtor = debtors.get(debtorIndex);
            SettlementParticipant creditor = creditors.get(creditorIndex);
            BigDecimal amount = debtor.amount.min(creditor.amount).setScale(2, RoundingMode.HALF_UP);

            if (amount.compareTo(ZERO) > 0) {
                settlements.add(new SettlementResponse(
                        debtor.memberId,
                        debtor.memberName,
                        creditor.memberId,
                        creditor.memberName,
                        amount
                ));
            }

            debtor.amount = debtor.amount.subtract(amount).setScale(2, RoundingMode.HALF_UP);
            creditor.amount = creditor.amount.subtract(amount).setScale(2, RoundingMode.HALF_UP);

            if (debtor.amount.compareTo(ZERO) == 0) {
                debtorIndex++;
            }

            if (creditor.amount.compareTo(ZERO) == 0) {
                creditorIndex++;
            }
        }

        return settlements;
    }

    private List<MemberBalanceResponse> calculateBalances(Long groupId) {
        List<Member> members = memberRepository.findByExpenseGroupId(groupId);

        if (members.isEmpty()) {
            return List.of();
        }

        List<Expense> expenses = expenseRepository.findByExpenseGroupId(groupId);
        BigDecimal totalExpenses = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal equalShare = totalExpenses
                .divide(BigDecimal.valueOf(members.size()), 2, RoundingMode.HALF_UP);
        Map<Long, BigDecimal> paidByMember = new LinkedHashMap<>();

        for (Member member : members) {
            paidByMember.put(member.getId(), ZERO);
        }

        for (Expense expense : expenses) {
            Long payerId = expense.getPayer().getId();
            BigDecimal currentPaid = paidByMember.getOrDefault(payerId, ZERO);
            paidByMember.put(payerId, currentPaid.add(expense.getAmount()).setScale(2, RoundingMode.HALF_UP));
        }

        List<MemberBalanceResponse> balances = new ArrayList<>();

        for (Member member : members) {
            BigDecimal paidAmount = paidByMember.get(member.getId()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal balance = paidAmount.subtract(equalShare).setScale(2, RoundingMode.HALF_UP);
            balances.add(new MemberBalanceResponse(
                    member.getId(),
                    member.getName(),
                    paidAmount,
                    equalShare,
                    balance
            ));
        }

        return balances;
    }

    private void validateGroupExists(Long groupId) {
        if (!expenseGroupRepository.existsById(groupId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found");
        }
    }

    private static class SettlementParticipant {
        private final Long memberId;
        private final String memberName;
        private BigDecimal amount;

        private SettlementParticipant(Long memberId, String memberName, BigDecimal amount) {
            this.memberId = memberId;
            this.memberName = memberName;
            this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        }
    }
}
