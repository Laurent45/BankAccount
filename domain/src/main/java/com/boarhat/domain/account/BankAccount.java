package com.boarhat.domain.account;

import com.boarhat.domain.exception.InsufficientFundsException;
import com.boarhat.domain.shared.Amount;
import com.boarhat.domain.shared.Balance;

import java.util.Objects;

public final class BankAccount extends Account {

    private OverdraftAuthorization overdraftAuthorization;

    public static BankAccount create(AccountId accountId) {
        return new BankAccount(accountId, Balance.zero(), OverdraftAuthorization.notAllowed());
    }

    public static BankAccount reconstruct(AccountId accountId, Balance balance, OverdraftAuthorization overdraftAuthorization) {
        return new BankAccount(accountId, balance, overdraftAuthorization);
    }

    private BankAccount(AccountId accountId, Balance balance, OverdraftAuthorization overdraftAuthorization) {
        super(accountId, balance);
        this.overdraftAuthorization = Objects.requireNonNull(overdraftAuthorization, "OverdraftAuthorization must not be null");
    }

    public void updateOverdraftAuthorization(OverdraftAuthorization overdraftAuthorization) {
        this.overdraftAuthorization = Objects.requireNonNull(overdraftAuthorization, "OverdraftAuthorization must not be null");
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.BANK_ACCOUNT;
    }

    @Override
    protected void doDeposit(Amount amount) {
        this.balance = this.balance.add(amount);
    }

    @Override
    protected void doWithdraw(Amount amount) {
        if (!isWithdrawAllowed(amount)) {
            throw new InsufficientFundsException(this.accountId, this.balance, amount);
        }
        this.balance = this.balance.subtract(amount);
    }

    public OverdraftAuthorization getOverdraftAuthorization() {
        return overdraftAuthorization;
    }

    private boolean isWithdrawAllowed(Amount amount) {
        Balance availableBalance = overdraftAuthorization.availableBalance(this.balance);
        return !availableBalance.subtract(amount).isNegative();
    }
}
