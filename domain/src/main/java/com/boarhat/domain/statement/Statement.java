package com.boarhat.domain.statement;

import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.account.AccountType;
import com.boarhat.domain.operation.Operation;
import com.boarhat.domain.shared.Balance;

import java.time.LocalDateTime;
import java.util.List;

public record Statement(
        AccountType accountType,
        AccountId accountId,
        LocalDateTime issuedAt,
        Balance balance,
        List<Operation> operations
) {
    public Statement {
        operations = List.copyOf(operations);
    }
}
