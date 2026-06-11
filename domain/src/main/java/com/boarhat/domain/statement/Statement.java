package com.boarhat.domain.statement;

import com.boarhat.domain.account.Account;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.account.AccountType;
import com.boarhat.domain.operation.Operation;
import com.boarhat.domain.shared.Balance;

import java.time.Instant;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;

public record Statement(
        AccountType accountType,
        AccountId accountId,
        Instant issuedAt,
        Balance balance,
        List<Operation> operations
) {
    public static final Period HISTORY_WINDOW = Period.ofMonths(1);

    public Statement {
        operations = List.copyOf(operations);
    }

    public static Statement of(Account account, Instant issuedAt, List<Operation> operations) {
        Instant windowStart = windowStart(issuedAt);

        List<Operation> recent = operations.stream()
                .filter(operation -> operation.occurredAt().isAfter(windowStart))
                .sorted(Comparator.comparing(Operation::occurredAt).reversed())
                .toList();

        return new Statement(account.getAccountType(), account.getAccountId(), issuedAt, account.getBalance(), recent);
    }

    // "One month ago" is calendar arithmetic, which an Instant alone cannot do; the window
    // is anchored on UTC so it is independent of wherever the application happens to run.
    public static Instant windowStart(Instant issuedAt) {
        return issuedAt.atZone(ZoneOffset.UTC).minus(HISTORY_WINDOW).toInstant();
    }
}
