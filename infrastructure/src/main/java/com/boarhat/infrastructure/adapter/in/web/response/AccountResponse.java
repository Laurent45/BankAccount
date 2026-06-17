package com.boarhat.infrastructure.adapter.in.web.response;

import com.boarhat.domain.account.Account;
import com.boarhat.domain.account.BankAccount;
import com.boarhat.domain.account.SavingsAccount;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AccountResponse(
        @Schema(description = "Account identifier", example = "b3f1c2d4-5e6f-7a8b-9c0d-1e2f3a4b5c6d")
        UUID accountId,
        @Schema(description = "Type of account", example = "BANK")
        String accountType,
        @Schema(description = "Current balance", example = "100.00")
        BigDecimal balance,
        @Schema(description = "Overdraft authorization limit (bank accounts only)", example = "200.00")
        BigDecimal overdraftLimit,
        @Schema(description = "Deposit ceiling (savings accounts only)", example = "10000.00")
        BigDecimal depositCeiling
) {
    public static AccountResponse from(Account account) {
        return switch (account) {
            case BankAccount bankAccount -> new AccountResponse(
                    bankAccount.getAccountId().value(),
                    bankAccount.getAccountType().name(),
                    toMoney(bankAccount.getBalance().value()),
                    toMoney(bankAccount.getOverdraftAuthorization().limit()),
                    null);
            case SavingsAccount savingsAccount -> new AccountResponse(
                    savingsAccount.getAccountId().value(),
                    savingsAccount.getAccountType().name(),
                    toMoney(savingsAccount.getBalance().value()),
                    null,
                    toMoney(savingsAccount.getDepositCeiling().amount().value()));
        };
    }

    private static BigDecimal toMoney(BigDecimal amount) {
        return amount == null ? null : amount.setScale(2, RoundingMode.HALF_UP);
    }
}
