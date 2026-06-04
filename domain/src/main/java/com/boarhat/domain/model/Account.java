package com.boarhat.domain.model;

import java.util.Objects;

public abstract class Account {
    protected final AccountId accountId;
    protected Money balance;

    protected Account(AccountId accountId, Money balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public AccountId getAccountId() {
        return this.accountId;
    }

    public Money getBalance() {
        return this.balance;
    }

    public abstract void deposit(Money amount);
    public abstract void withdraw(Money amount);

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
