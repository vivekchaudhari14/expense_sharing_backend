package com.expense_sharing_backend.dto;

import java.math.BigDecimal;

public record SettlementTransaction(
        Long fromUserId,
        Long toUserId,
        BigDecimal amount
) {}
