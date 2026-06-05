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
        return new SavingsAccount(accountId, Balance.zero(), depositCeiling, List.of());
    }

    public static SavingsAccount reconstruct(AccountId accountId, Balance balance, DepositCeiling depositCeiling, List<Operation> operations) {
        return new SavingsAccount(accountId, balance, depositCeiling, operations);
    }

    private SavingsAccount(AccountId accountId, Balance balance, DepositCeiling depositCeiling, List<Operation> operations) {
        super(accountId, balance, operations);
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

    private boolean isDepositCeilingReached(Amount amount) {
        Balance maxBalance = Balance.of(depositCeiling.amount());
        return this.balance.add(amount).isGreaterThan(maxBalance);
    }

    @Override
    protected void doWithdraw(Amount amount) {
        if (this.balance.subtract(amount).isNegative()) {
            throw new InsufficientFundsException(amount, this.accountId, this.balance);
        }
        this.balance = this.balance.subtract(amount);
    }
}
