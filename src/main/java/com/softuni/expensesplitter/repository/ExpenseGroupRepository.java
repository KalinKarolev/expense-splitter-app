package com.softuni.expensesplitter.repository;

import com.softuni.expensesplitter.entity.ExpenseGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseGroupRepository extends JpaRepository<ExpenseGroup, Long> {
}
