package com.boarhat.domain.statement;

import com.boarhat.domain.account.Account;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.account.AccountType;
import com.boarhat.domain.operation.Operation;
import com.boarhat.domain.shared.Balance;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.Comparator;
import java.util.List;

public record Statement(
        AccountType accountType,
        AccountId accountId,
        LocalDateTime issuedAt,
        Balance balance,
        List<Operation> operations
) {
    public static final Period HISTORY_WINDOW = Period.ofMonths(1);

    public Statement {
        operations = List.copyOf(operations);
    }

    public static Statement of(Account account, LocalDateTime issuedAt, List<Operation> operations) {
        LocalDateTime windowStart = issuedAt.minus(HISTORY_WINDOW);

        List<Operation> recent = operations.stream()
                .filter(operation -> operation.occurredAt().isAfter(windowStart))
                .sorted(Comparator.comparing(Operation::occurredAt).reversed())
                .toList();

        return new Statement(account.getAccountType(), account.getAccountId(), issuedAt, account.getBalance(), recent);
    }
}
