package com.expense_sharing_backend.repository;

import com.expense_sharing_backend.model.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByGroupId(Long groupId);
    Page<Expense> findByGroupIdOrderByCreatedAtDesc(Long groupId, Pageable pageable);
    List<Expense> findByPaidByUserId(Long userId);
}

