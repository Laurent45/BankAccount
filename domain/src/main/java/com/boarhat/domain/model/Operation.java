package com.boarhat.domain.model;

import java.time.LocalDateTime;

public record Operation(OperationType type, Amount amount, Balance balance, LocalDateTime occurredAt) {
}
