package com.expense_sharing_backend.service;

import com.expense_sharing_backend.dto.ExpenseRequests;
import com.expense_sharing_backend.model.Expense;
import com.expense_sharing_backend.model.Group;
import com.expense_sharing_backend.model.Split;
import com.expense_sharing_backend.model.User;
import com.expense_sharing_backend.repository.ExpenseRepository;
import com.expense_sharing_backend.repository.GroupRepository;
import com.expense_sharing_backend.repository.UserRepository;
import com.expense_sharing_backend.strategy.ExpenseSplitStrategy;
import com.expense_sharing_backend.strategy.SplitStrategyFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final SplitStrategyFactory splitStrategyFactory;

    public ExpenseService(ExpenseRepository expenseRepository,
                          UserRepository userRepository,
                          GroupRepository groupRepository,
                          SplitStrategyFactory splitStrategyFactory) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.splitStrategyFactory = splitStrategyFactory;
    }

    @Transactional
    public Expense createExpense(ExpenseRequests.CreateExpenseRequest request) {
        User payer = userRepository.findById(request.paidByUserId())
                .orElseThrow(() -> new IllegalArgumentException("Payer not found: " + request.paidByUserId()));

        Group group = null;
        if (request.groupId() != null) {
            group = groupRepository.findById(request.groupId())
                    .orElseThrow(() -> new IllegalArgumentException("Group not found: " + request.groupId()));
        }

        ExpenseSplitStrategy strategy = splitStrategyFactory.get(request.type());
        Map<Long, BigDecimal> splitAmounts = strategy.calculateSplits(request.totalAmount(), request.splits());

        Expense expense = Expense.builder()
                .description(request.description())
                .totalAmount(request.totalAmount())
                .paidBy(payer)
                .group(group)
                .type(request.type())
                .build();

        for (Map.Entry<Long, BigDecimal> entry : splitAmounts.entrySet()) {
            User participant = userRepository.findById(entry.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + entry.getKey()));

            Split split = Split.builder()
                    .user(participant)
                    .amount(entry.getValue())
                    .build();

            expense.addSplit(split);
        }

        return expenseRepository.save(expense);
    }
}
