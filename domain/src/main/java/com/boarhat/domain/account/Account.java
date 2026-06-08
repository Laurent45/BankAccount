package com.boarhat.domain.account;

import com.boarhat.domain.operation.Operation;
import com.boarhat.domain.operation.OperationType;
import com.boarhat.domain.shared.Amount;
import com.boarhat.domain.shared.Balance;
import com.boarhat.domain.statement.Statement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public abstract sealed class Account permits BankAccount, SavingsAccount {

    private final List<Operation> operations;
    protected final AccountId accountId;
    protected Balance balance;

    protected Account(AccountId accountId, Balance balance, List<Operation> operations) {
        this.accountId = accountId;
        this.balance = balance;
        this.operations = new ArrayList<>(operations);
    }

    public AccountId getAccountId() {
        return this.accountId;
    }

    public Balance getBalance() {
        return this.balance;
    }

    public final void deposit(Amount amount, LocalDateTime occurredAt) {
        doDeposit(amount);
        operations.add(new Operation(OperationType.DEPOSIT, amount, this.balance, occurredAt));
    }

    public final void withdraw(Amount amount, LocalDateTime occurredAt) {
        doWithdraw(amount);
        operations.add(new Operation(OperationType.WITHDRAWAL, amount, this.balance, occurredAt));
    }

    public Statement getStatement(LocalDateTime now) {
        LocalDateTime oneMonthAgo = now.minusMonths(1);

        List<Operation> filtered = operations.stream()
                .filter(op -> op.occurredAt().isAfter(oneMonthAgo))
                .sorted(Comparator.comparing(Operation::occurredAt).reversed())
                .toList();

        return new Statement(getAccountType(), getAccountId(), now, getBalance(), filtered);
    }

    public abstract AccountType getAccountType();
    protected abstract void doDeposit(Amount amount);
    protected abstract void doWithdraw(Amount amount);

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Account account)) return false;
        return Objects.equals(accountId, account.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(accountId);
    }
}
