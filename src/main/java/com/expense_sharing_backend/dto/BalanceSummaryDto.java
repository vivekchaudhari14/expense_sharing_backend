package com.expense_sharing_backend.dto;

import java.math.BigDecimal;

public record BalanceSummaryDto(
        Long userId,
        BigDecimal netBalance
) {}

