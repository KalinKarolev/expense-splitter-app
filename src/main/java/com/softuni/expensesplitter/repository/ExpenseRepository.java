package com.softuni.expensesplitter.repository;

import com.softuni.expensesplitter.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByExpenseGroupId(Long expenseGroupId);
}
