package com.boarhat.domain.account;

import com.boarhat.domain.exception.DepositCeilingReachedException;
import com.boarhat.domain.exception.InsufficientFundsException;
import com.boarhat.domain.operation.Operation;
import com.boarhat.domain.shared.Amount;
import com.boarhat.domain.shared.Balance;

import java.util.List;

public final class SavingsAccount extends Account {

    private final DepositCeiling depositCeiling;

    public static SavingsAccount create(AccountId accountId, DepositCeiling depositCeiling) {
        return new SavingsAccount(accountId, Balance.zero(), depositCeiling);
    }

    public SavingsAccount(AccountId accountId, Balance balance, DepositCeiling depositCeiling) {
        super(accountId, balance);
        this.depositCeiling = depositCeiling;
    }

    public SavingsAccount(AccountId accountId, Balance balance, DepositCeiling depositCeiling, List<Operation> operations) {
        super(accountId, balance, operations);
        this.depositCeiling = depositCeiling;
    }

    @Override
    protected void doDeposit(Amount amount) {
        if (isDepositCeilingReached(amount)) {
            throw new DepositCeilingReachedException(accountId, balance, amount, depositCeiling);
        }
        this.balance = this.balance.add(amount);
    }

    private boolean isDepositCeilingReached(Amount amount) {
        Balance maxBalance = Balance.of(depositCeiling.amount());
        return this.balance.add(amount).isGreaterThan(maxBalance);
    }

    @Override
    protected void doWithdraw(Amount amount) {
        if (Balance.of(amount).isGreaterThan(this.balance)) {
            throw new InsufficientFundsException(amount, this.accountId, this.balance);
        }
        this.balance = this.balance.subtract(amount);
    }
}
