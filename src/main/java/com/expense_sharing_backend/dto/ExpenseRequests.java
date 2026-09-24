package com.expense_sharing_backend.dto;

import com.expense_sharing_backend.model.ExpenseType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class ExpenseRequests {

    public record SplitDetail(
            @NotNull Long userId,
            BigDecimal amount,
            BigDecimal percentage
    ) {}

    public record CreateExpenseRequest(
            @NotBlank String description,
            @NotNull @DecimalMin("0.01") BigDecimal totalAmount,
            @NotNull Long paidByUserId,
            Long groupId,
            @NotNull ExpenseType type,
            @NotEmpty List<SplitDetail> splits
    ) {}
}
