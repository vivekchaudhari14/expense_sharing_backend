package com.expense_sharing_backend.controller;

import com.expense_sharing_backend.dto.ExpenseRequests;
import com.expense_sharing_backend.dto.SettlementTransaction;
import com.expense_sharing_backend.model.Expense;
import com.expense_sharing_backend.repository.ExpenseJdbcRepository;
import com.expense_sharing_backend.service.DebtSimplificationService;
import com.expense_sharing_backend.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ExpenseJdbcRepository jdbcRepository;
    private final DebtSimplificationService debtSimplificationService;

    public ExpenseController(ExpenseService expenseService,
                             ExpenseJdbcRepository jdbcRepository,
                             DebtSimplificationService debtSimplificationService) {
        this.expenseService = expenseService;
        this.jdbcRepository = jdbcRepository;
        this.debtSimplificationService = debtSimplificationService;
    }

    @PostMapping("/expenses")
    public ResponseEntity<Long> createExpense(@RequestBody @Valid ExpenseRequests.CreateExpenseRequest request) {
        Expense expense = expenseService.createExpense(request);
        return ResponseEntity.ok(expense.getId());
    }

    @GetMapping("/groups/{groupId}/settlements")
    public ResponseEntity<List<SettlementTransaction>> getSettlementPlan(@PathVariable Long groupId) {
        var balances = jdbcRepository.getGroupNetBalances(groupId);
        List<SettlementTransaction> transactions = debtSimplificationService.simplifyDebts(balances);
        return ResponseEntity.ok(transactions);
    }
}
