package com.boarhat.application.command;

import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.shared.Amount;

public record WithdrawCommand(AccountId accountId, Amount amount) {}
