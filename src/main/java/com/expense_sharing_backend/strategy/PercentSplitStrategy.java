package com.expense_sharing_backend.strategy;

import com.expense_sharing_backend.dto.ExpenseRequests;
import com.expense_sharing_backend.model.ExpenseType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PercentSplitStrategy implements ExpenseSplitStrategy {

    @Override
    public ExpenseType getSupportedType() {
        return ExpenseType.PERCENT;
    }

    @Override
    public Map<Long, BigDecimal> calculateSplits(BigDecimal totalAmount, List<ExpenseRequests.SplitDetail> splits) {
        BigDecimal totalPercent = BigDecimal.ZERO;
        Map<Long, BigDecimal> results = new HashMap<>();
        BigDecimal allocated = BigDecimal.ZERO;

        for (ExpenseRequests.SplitDetail split : splits) {
            if (split.percentage() == null || split.percentage().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Percent must be positive for user: " + split.userId());
            }
            totalPercent = totalPercent.add(split.percentage());
        }

        if (totalPercent.compareTo(new BigDecimal("100")) != 0) {
            throw new IllegalArgumentException("Percentages must sum to 100. Current sum: " + totalPercent);
        }

        for (int i = 0; i < splits.size(); i++) {
            ExpenseRequests.SplitDetail s = splits.get(i);
            BigDecimal share = totalAmount.multiply(s.percentage())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            results.put(s.userId(), share);
            allocated = allocated.add(share);
        }

        // Adjust 1-2 cent discrepancies caused by rounding
        BigDecimal diff = totalAmount.subtract(allocated);
        if (diff.compareTo(BigDecimal.ZERO) != 0) {
            Long firstUser = splits.getFirst().userId();
            results.put(firstUser, results.get(firstUser).add(diff));
        }

        return results;
    }
}

