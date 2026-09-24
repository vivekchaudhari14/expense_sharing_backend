package com.expense_sharing_backend.strategy;

import com.expense_sharing_backend.dto.ExpenseRequests;
import com.expense_sharing_backend.model.ExpenseType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ExpenseSplitStrategy {
    ExpenseType getSupportedType();
    Map<Long, BigDecimal> calculateSplits(BigDecimal totalAmount, List<ExpenseRequests.SplitDetail> splits);
}
