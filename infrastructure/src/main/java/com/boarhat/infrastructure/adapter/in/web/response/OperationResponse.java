package com.boarhat.infrastructure.adapter.in.web.response;

import com.boarhat.domain.operation.Operation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OperationResponse(
        String type,
        BigDecimal amount,
        BigDecimal balance,
        LocalDateTime occurredAt
) {
    static OperationResponse from(Operation operation) {
        return new OperationResponse(
                operation.type().name(),
                operation.amount().value(),
                operation.balance().value(),
                operation.occurredAt());
    }
}
