package com.expense_sharing_backend.service;

import com.expense_sharing_backend.dto.BalanceSummaryDto;
import com.expense_sharing_backend.dto.SettlementTransaction;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;

@Service
public class DebtSimplificationService {

    private record ParticipantBalance(Long userId, BigDecimal balance) {}

    /**
     * Solves Min Cash Flow using two priority queues (Max-Heaps).
     * Time Complexity: O(N log N)
     */
    public List<SettlementTransaction> simplifyDebts(List<BalanceSummaryDto> balances) {
        // Max-heap for net creditors (people who are owed money)
        PriorityQueue<ParticipantBalance> creditors = new PriorityQueue<>(
                (a, b) -> b.balance().compareTo(a.balance())
        );

        // Max-heap for net debtors (people who owe money) by absolute magnitude
        PriorityQueue<ParticipantBalance> debtors = new PriorityQueue<>(
                (a, b) -> b.balance().compareTo(a.balance())
        );

        for (BalanceSummaryDto b : balances) {
            if (b.netBalance().compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(new ParticipantBalance(b.userId(), b.netBalance()));
            } else if (b.netBalance().compareTo(BigDecimal.ZERO) < 0) {
                debtors.add(new ParticipantBalance(b.userId(), b.netBalance().abs()));
            }
        }

        List<SettlementTransaction> settlements = new ArrayList<>();

        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            ParticipantBalance maxCreditor = creditors.poll();
            ParticipantBalance maxDebtor = debtors.poll();

            BigDecimal settledAmount = maxCreditor.balance().min(maxDebtor.balance());

            settlements.add(new SettlementTransaction(
                    maxDebtor.userId(),
                    maxCreditor.userId(),
                    settledAmount
            ));

            BigDecimal creditorRemaining = maxCreditor.balance().subtract(settledAmount);
            BigDecimal debtorRemaining = maxDebtor.balance().subtract(settledAmount);

            if (creditorRemaining.compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(new ParticipantBalance(maxCreditor.userId(), creditorRemaining));
            }
            if (debtorRemaining.compareTo(BigDecimal.ZERO) > 0) {
                debtors.add(new ParticipantBalance(maxDebtor.userId(), debtorRemaining));
            }
        }

        return settlements;
    }
}

