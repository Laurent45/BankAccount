package com.boarhat.domain.account;

import com.boarhat.domain.exception.DepositCeilingReachedException;
import com.boarhat.domain.exception.InsufficientFundsException;
import com.boarhat.domain.shared.Amount;
import com.boarhat.domain.shared.Balance;

import java.util.Objects;

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
        this.depositCeiling = Objects.requireNonNull(depositCeiling, "DepositCeiling must not be null");
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.SAVINGS_ACCOUNT;
    }

    @Override
    protected void doDeposit(Amount amount) {
        Balance newBalance = this.balance.add(amount);
        if (depositCeiling.isExceededBy(newBalance)) {
            throw new DepositCeilingReachedException(accountId, balance, amount, depositCeiling);
        }
        this.balance = newBalance;
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
}
