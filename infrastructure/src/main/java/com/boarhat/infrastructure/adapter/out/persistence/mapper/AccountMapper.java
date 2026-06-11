package com.boarhat.infrastructure.adapter.out.persistence.mapper;

import com.boarhat.domain.account.Account;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.account.AccountType;
import com.boarhat.domain.account.BankAccount;
import com.boarhat.domain.account.DepositCeiling;
import com.boarhat.domain.account.OverdraftAuthorization;
import com.boarhat.domain.account.SavingsAccount;
import com.boarhat.domain.shared.Amount;
import com.boarhat.domain.shared.Balance;
import com.boarhat.infrastructure.adapter.out.persistence.entity.AccountJpaEntity;

public final class AccountMapper {

    private AccountMapper() {
    }

    public static AccountJpaEntity toEntity(Account account) {
        return switch (account) {
            case BankAccount bankAccount -> new AccountJpaEntity(
                    bankAccount.getAccountId().value(),
                    AccountType.BANK_ACCOUNT,
                    bankAccount.getBalance().value(),
                    bankAccount.getOverdraftAuthorization().limit(),
                    null);
            case SavingsAccount savingsAccount -> new AccountJpaEntity(
                    savingsAccount.getAccountId().value(),
                    AccountType.SAVINGS_ACCOUNT,
                    savingsAccount.getBalance().value(),
                    null,
                    savingsAccount.getDepositCeiling().amount().value());
        };
    }

    public static Account toDomain(AccountJpaEntity entity) {
        return switch (entity.getType()) {
            case BANK_ACCOUNT -> BankAccount.reconstruct(
                    AccountId.of(entity.getId()),
                    Balance.of(entity.getBalance()),
                    new OverdraftAuthorization(entity.getOverdraftLimit()));
            case SAVINGS_ACCOUNT -> SavingsAccount.reconstruct(
                    AccountId.of(entity.getId()),
                    Balance.of(entity.getBalance()),
                    new DepositCeiling(Amount.of(entity.getDepositCeiling())));
        };
    }
}
