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
public class EqualSplitStrategy implements ExpenseSplitStrategy {

    @Override
    public ExpenseType getSupportedType() {
        return ExpenseType.EQUAL;
    }

    @Override
    public Map<Long, BigDecimal> calculateSplits(BigDecimal totalAmount, List<ExpenseRequests.SplitDetail> splits) {
        int count = splits.size();
        if (count == 0) throw new IllegalArgumentException("Splits list cannot be empty");

        BigDecimal baseShare = totalAmount.divide(BigDecimal.valueOf(count), 2, RoundingMode.DOWN);
        BigDecimal remainder = totalAmount.subtract(baseShare.multiply(BigDecimal.valueOf(count)));

        Map<Long, BigDecimal> results = new HashMap<>();
        for (int i = 0; i < count; i++) {
            BigDecimal share = baseShare;
            // Absorb cent rounding difference onto the first participant
            if (i == 0) {
                share = share.add(remainder);
            }
            results.put(splits.get(i).userId(), share);
        }
        return results;
    }
}
