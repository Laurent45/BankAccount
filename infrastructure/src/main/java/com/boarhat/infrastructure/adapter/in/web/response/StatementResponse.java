package com.boarhat.infrastructure.adapter.in.web.response;

import com.boarhat.domain.statement.Statement;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record StatementResponse(
        String accountType,
        UUID accountId,
        LocalDateTime issuedAt,
        BigDecimal balance,
        List<OperationResponse> operations
) {
    public static StatementResponse from(Statement statement) {
        return new StatementResponse(
                statement.accountType().name(),
                statement.accountId().value(),
                statement.issuedAt(),
                statement.balance().value(),
                statement.operations().stream().map(OperationResponse::from).toList());
    }
}
