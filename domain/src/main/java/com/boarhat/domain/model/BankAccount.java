package com.boarhat.domain.model;

import com.boarhat.domain.exception.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.Objects;

public class BankAccount implements Account {
    private final AccountId accountId;
    private Money balance;
    private OverdraftAuthorization overdraftAuthorization;

    public static BankAccount create(AccountId accountId) {
        return new BankAccount(accountId, Money.of(BigDecimal.ZERO), OverdraftAuthorization.notAllowed());
    }

    public BankAccount(AccountId accountId, Money balance, OverdraftAuthorization overdraftAuthorization) {
        this.accountId = accountId;
        this.balance = balance;
        this.overdraftAuthorization = overdraftAuthorization;
    }

    public void allowOverdraft(OverdraftAuthorization overdraftAuthorization) {
        this.overdraftAuthorization = overdraftAuthorization;
    }

    public void denyOverdraft() {
        this.overdraftAuthorization = OverdraftAuthorization.notAllowed();
    }

    @Override
    public void deposit(Money amount) {
        this.balance = this.balance.add(amount);
    }

    @Override
    public void withdraw(Money amount) {
        if (!isWithdrawAllowed(amount)) {
            throw new InsufficientFundsException(amount, this.accountId, this.balance);
        }
        this.balance = this.balance.subtract(amount);
    }

    @Override
    public AccountId getAccountId() {
        return accountId;
    }

    @Override
    public Money getBalance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BankAccount that)) return false;
        return Objects.equals(accountId, that.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(accountId);
    }

    private boolean isWithdrawAllowed(Money amount) {
        Money available = this.balance.add(overdraftAuthorization.amount());
        return !amount.isGreaterThan(available);
    }
}
