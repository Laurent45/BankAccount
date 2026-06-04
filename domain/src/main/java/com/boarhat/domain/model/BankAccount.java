package com.boarhat.domain.model;

import com.boarhat.domain.exception.InsufficientFundsException;

public class BankAccount extends Account {
    private OverdraftAuthorization overdraftAuthorization;

    public static BankAccount create(AccountId accountId) {
        return new BankAccount(accountId, Balance.zero(), OverdraftAuthorization.notAllowed());
    }

    public BankAccount(AccountId accountId, Balance balance, OverdraftAuthorization overdraftAuthorization) {
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
    public void deposit(Amount amount) {
        this.balance = this.balance.add(amount);
    }

    @Override
    public void withdraw(Amount amount) {
        if (!isWithdrawAllowed(amount)) {
            throw new InsufficientFundsException(amount, this.accountId, this.balance);
        }
        this.balance = this.balance.subtract(amount);
    }

    private boolean isWithdrawAllowed(Amount amount) {
        Balance availableBalance = this.balance.add(overdraftAuthorization.limit());
        return !availableBalance.subtract(amount).isNegative();
    }
}
