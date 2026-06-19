package com.softuni.expensesplitter.dto;

import java.math.BigDecimal;

public class MemberBalanceResponse {

    private Long memberId;
    private String memberName;
    private BigDecimal paidAmount;
    private BigDecimal equalShare;
    private BigDecimal balance;

    public MemberBalanceResponse() {
    }

    public MemberBalanceResponse(Long memberId, String memberName, BigDecimal paidAmount, BigDecimal equalShare, BigDecimal balance) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.paidAmount = paidAmount;
        this.equalShare = equalShare;
        this.balance = balance;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getEqualShare() {
        return equalShare;
    }

    public void setEqualShare(BigDecimal equalShare) {
        this.equalShare = equalShare;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
