package com.softuni.expensesplitter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    @NotNull
    @Positive
    private BigDecimal amount;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_group_id", nullable = false)
    @NotNull
    private ExpenseGroup expenseGroup;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id", nullable = false)
    @NotNull
    private Member payer;

    public Expense() {
    }

    public Expense(String description, BigDecimal amount, ExpenseGroup expenseGroup, Member payer) {
        this.description = description;
        this.amount = amount;
        this.expenseGroup = expenseGroup;
        this.payer = payer;
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

    public ExpenseGroup getExpenseGroup() {
        return expenseGroup;
    }

    public void setExpenseGroup(ExpenseGroup expenseGroup) {
        this.expenseGroup = expenseGroup;
    }

    public Member getPayer() {
        return payer;
    }

    public void setPayer(Member payer) {
        this.payer = payer;
    }
}
