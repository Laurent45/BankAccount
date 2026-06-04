package com.boarhat.domain.model;

import java.time.LocalDateTime;
import java.util.*;

public abstract sealed class Account permits BankAccount, SavingsAccount {
    private final List<Operation> operations;
    protected final AccountId accountId;
    protected Balance balance;

    protected Account(AccountId accountId, Balance balance, List<Operation> operations) {
        this.accountId = accountId;
        this.balance = balance;
        this.operations = new ArrayList<>(operations);
    }

    protected Account(AccountId accountId, Balance balance) {
        this(accountId, balance, new ArrayList<>());
    }

    public AccountId getAccountId() {
        return this.accountId;
    }

    public Balance getBalance() {
        return this.balance;
    }

    public final void deposit(Amount amount) {
        doDeposit(amount);

        Operation operation = new Operation(
                OperationType.DEPOSIT,
                amount,
                this.balance,
                LocalDateTime.now()
        );
        operations.add(operation);
    }

    public final void withdraw(Amount amount) {
        doWithdraw(amount);

        Operation operation = new Operation(
                OperationType.WITHDRAW,
                amount,
                this.balance,
                LocalDateTime.now()
        );
        operations.add(operation);
    }

    public Statement getStatement() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthAgo = now.minusMonths(1);

        List<Operation> operationSorted = operations.stream()
                .filter(operation -> operation.occurredAt().isAfter(oneMonthAgo))
                .sorted(Comparator.comparing(Operation::occurredAt).reversed())
                .toList();

        return new Statement(
                getAccountType(),
                getAccountId(),
                now,
                getBalance(),
                operationSorted
        );
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
