package com.boarhat.domain.account;

import com.boarhat.domain.exception.DepositCeilingReachedException;
import com.boarhat.domain.exception.InsufficientFundsException;
import com.boarhat.domain.shared.Amount;
import com.boarhat.domain.shared.Balance;

public final class SavingsAccount extends Account {

    private final DepositCeiling depositCeiling;

    public static SavingsAccount create(AccountId accountId, DepositCeiling depositCeiling) {
        return new SavingsAccount(accountId, Balance.zero(), depositCeiling);
    }

    public static SavingsAccount reconstruct(AccountId accountId, Balance balance, DepositCeiling depositCeiling) {
        return new SavingsAccount(accountId, balance, depositCeiling);
    }

    private SavingsAccount(AccountId accountId, Balance balance, DepositCeiling depositCeiling) {
        super(accountId, balance);
        this.depositCeiling = depositCeiling;
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.SAVINGS_ACCOUNT;
    }

    @Override
    protected void doDeposit(Amount amount) {
        if (isDepositCeilingReached(amount)) {
            throw new DepositCeilingReachedException(accountId, balance, amount, depositCeiling);
        }
        this.balance = this.balance.add(amount);
    }

    @Override
    protected void doWithdraw(Amount amount) {
        Balance newBalance = this.balance.subtract(amount);
        if (newBalance.isNegative()) {
            throw new InsufficientFundsException(this.accountId, this.balance, amount);
        }
        this.balance = newBalance;
    }

    public DepositCeiling getDepositCeiling() {
        return depositCeiling;
    }

    private boolean isDepositCeilingReached(Amount amount) {
        Balance maxBalance = Balance.of(depositCeiling.amount());
        return this.balance.add(amount).isGreaterThan(maxBalance);
    }
}
