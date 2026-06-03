package com.boarhat.domain.model;

public interface Account {
    void deposit(Money money);
    void withdraw(Money money);
    AccountId getAccountId();
    Money getBalance();
}
