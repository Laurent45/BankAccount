package com.boarhat.domain.operation;

import com.boarhat.domain.shared.Amount;
import com.boarhat.domain.shared.Balance;

import java.time.LocalDateTime;

public record Operation(OperationType type, Amount amount, Balance balance, LocalDateTime occurredAt) {
}
