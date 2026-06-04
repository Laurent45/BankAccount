package com.boarhat.domain.model;

import com.boarhat.domain.exception.InsufficientFundsException;

import java.math.BigDecimal;

public class BankAccount extends Account {
    private OverdraftAuthorization overdraftAuthorization;

    public static BankAccount create(AccountId accountId) {
        return new BankAccount(accountId, Money.of(BigDecimal.ZERO), OverdraftAuthorization.notAllowed());
    }

    public BankAccount(AccountId accountId, Money balance, OverdraftAuthorization overdraftAuthorization) {
        super(accountId, balance);
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

    private boolean isWithdrawAllowed(Money amount) {
        Money available = this.balance.add(overdraftAuthorization.amount());
        return !amount.isGreaterThan(available);
    }
}
