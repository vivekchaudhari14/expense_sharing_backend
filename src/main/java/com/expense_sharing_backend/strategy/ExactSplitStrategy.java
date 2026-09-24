package com.expense_sharing_backend.strategy;


import com.expense_sharing_backend.dto.ExpenseRequests;
import com.expense_sharing_backend.model.ExpenseType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExactSplitStrategy implements ExpenseSplitStrategy {

    @Override
    public ExpenseType getSupportedType() {
        return ExpenseType.EXACT;
    }

    @Override
    public Map<Long, BigDecimal> calculateSplits(BigDecimal totalAmount, List<ExpenseRequests.SplitDetail> splits) {
        Map<Long, BigDecimal> results = new HashMap<>();
        BigDecimal sum = BigDecimal.ZERO;

        for (ExpenseRequests.SplitDetail split : splits) {
            if (split.amount() == null || split.amount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Exact amount must be positive for user: " + split.userId());
            }
            results.put(split.userId(), split.amount());
            sum = sum.add(split.amount());
        }

        if (sum.compareTo(totalAmount) != 0) {
            throw new IllegalArgumentException("Sum of exact splits (" + sum + ") does not equal total (" + totalAmount + ")");
        }
        return results;
    }
}