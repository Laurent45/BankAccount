package com.boarhat.domain.operation;

import com.boarhat.domain.shared.Amount;
import com.boarhat.domain.shared.Balance;

import java.time.Instant;

public record Operation(OperationType type, Amount amount, Balance balance, Instant occurredAt) {
}
