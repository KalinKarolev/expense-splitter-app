package com.softuni.expensesplitter.repository;

import com.softuni.expensesplitter.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByExpenseGroupId(Long expenseGroupId);
}
