package com.expense_sharing_backend.repository;

import com.expense_sharing_backend.dto.BalanceSummaryDto;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ExpenseJdbcRepository {

    private final JdbcClient jdbcClient;

    public ExpenseJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    /**
     * Calculates net balance (total paid minus total owed) for all participants in a group
     * using a single optimized SQL union aggregation.
     */
    public List<BalanceSummaryDto> getGroupNetBalances(Long groupId) {
        String sql = """
            WITH combined AS (
                -- Amounts paid by the user in this group (credits)
                SELECT paid_by_user_id AS user_id, total_amount AS amount
                FROM expenses
                WHERE group_id = :groupId

                UNION ALL

                -- Amounts owed by the user in this group (debits)
                SELECT s.user_id AS user_id, -s.amount AS amount
                FROM splits s
                JOIN expenses e ON s.expense_id = e.id
                WHERE e.group_id = :groupId
            )
            SELECT user_id, COALESCE(SUM(amount), 0) AS net_balance
            FROM combined
            GROUP BY user_id
            HAVING SUM(amount) != 0
        """;

        return jdbcClient.sql(sql)
                .param("groupId", groupId)
                .query((rs, rowNum) -> new BalanceSummaryDto(
                        rs.getLong("user_id"),
                        rs.getBigDecimal("net_balance")
                ))
                .list();
    }
}

