package com.boarhat.domain.account;

import com.boarhat.domain.operation.Operation;
import com.boarhat.domain.operation.OperationType;
import com.boarhat.domain.shared.Amount;
import com.boarhat.domain.shared.Balance;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract sealed class Account permits BankAccount, SavingsAccount {

    protected final AccountId accountId;
    protected Balance balance;

    protected Account(AccountId accountId, Balance balance) {
        this.accountId = Objects.requireNonNull(accountId, "AccountId must not be null");
        this.balance = Objects.requireNonNull(balance, "Balance must not be null");
    }

    public AccountId getAccountId() {
        return this.accountId;
    }

    public Balance getBalance() {
        return this.balance;
    }

    public final Operation deposit(Amount amount, LocalDateTime occurredAt) {
        doDeposit(amount);
        return new Operation(OperationType.DEPOSIT, amount, this.balance, occurredAt);
    }

    public final Operation withdraw(Amount amount, LocalDateTime occurredAt) {
        doWithdraw(amount);
        return new Operation(OperationType.WITHDRAWAL, amount, this.balance, occurredAt);
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
