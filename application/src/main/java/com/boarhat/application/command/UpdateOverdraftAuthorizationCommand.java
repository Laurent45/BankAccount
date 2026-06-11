package com.boarhat.application.command;

import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.account.OverdraftAuthorization;

public record UpdateOverdraftAuthorizationCommand(AccountId accountId, OverdraftAuthorization overdraftAuthorization) {
}
