package com.boarhat.domain.model;

import java.util.Objects;

public abstract class Account {
    protected final AccountId accountId;
    protected Balance balance;

    protected Account(AccountId accountId, Balance balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public AccountId getAccountId() {
        return this.accountId;
    }

    public Balance getBalance() {
        return this.balance;
    }

    public abstract void deposit(Amount amount);
    public abstract void withdraw(Amount amount);

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
