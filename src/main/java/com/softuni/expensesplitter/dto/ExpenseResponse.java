package com.softuni.expensesplitter.dto;

import java.math.BigDecimal;

public class ExpenseResponse {

    private Long id;
    private String description;
    private BigDecimal amount;
    private Long groupId;
    private Long payerId;
    private String payerName;

    public ExpenseResponse() {
    }

    public ExpenseResponse(Long id, String description, BigDecimal amount, Long groupId, Long payerId, String payerName) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.groupId = groupId;
        this.payerId = payerId;
        this.payerName = payerName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getPayerId() {
        return payerId;
    }

    public void setPayerId(Long payerId) {
        this.payerId = payerId;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }
}
