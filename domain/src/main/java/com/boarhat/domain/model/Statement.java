package com.boarhat.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public record Statement(
        AccountType accountType,
        AccountId accountId,
        LocalDateTime issuedAt,
        Balance balance,
        List<Operation> operations
) {
}
