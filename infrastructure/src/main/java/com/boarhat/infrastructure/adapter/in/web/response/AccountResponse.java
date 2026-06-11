package com.boarhat.infrastructure.adapter.in.web.response;

import com.boarhat.domain.account.Account;
import com.boarhat.domain.account.BankAccount;
import com.boarhat.domain.account.SavingsAccount;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AccountResponse(
        UUID accountId,
        String accountType,
        BigDecimal balance,
        BigDecimal overdraftLimit,
        BigDecimal depositCeiling
) {
    public static AccountResponse from(Account account) {
        return switch (account) {
            case BankAccount bankAccount -> new AccountResponse(
                    bankAccount.getAccountId().value(),
                    bankAccount.getAccountType().name(),
                    bankAccount.getBalance().value(),
                    bankAccount.getOverdraftAuthorization().limit().value(),
                    null);
            case SavingsAccount savingsAccount -> new AccountResponse(
                    savingsAccount.getAccountId().value(),
                    savingsAccount.getAccountType().name(),
                    savingsAccount.getBalance().value(),
                    null,
                    savingsAccount.getDepositCeiling().amount().value());
        };
    }
}
