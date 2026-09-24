package com.expense_sharing_backend.strategy;

import com.expense_sharing_backend.model.ExpenseType;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SplitStrategyFactory {
    private final Map<ExpenseType, ExpenseSplitStrategy> strategies;

    public SplitStrategyFactory(List<ExpenseSplitStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(ExpenseSplitStrategy::getSupportedType, Function.identity()));
    }

    public ExpenseSplitStrategy get(ExpenseType type) {
        ExpenseSplitStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new UnsupportedOperationException("No strategy configured for type: " + type);
        }
        return strategy;
    }
}

