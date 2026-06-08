package com.boarhat.application.command;

import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.shared.Amount;

public record DepositCommand(AccountId accountId, Amount amount) {
}
