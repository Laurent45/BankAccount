package com.boarhat.application.service;

import com.boarhat.application.command.CreateSavingsAccountCommand;
import com.boarhat.application.port.in.CreateSavingsAccountUseCase;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.account.SavingsAccount;
import com.boarhat.domain.port.out.AccountRepository;

import java.util.UUID;

public class CreateSavingsAccountService implements CreateSavingsAccountUseCase {

    private final AccountRepository accountRepository;

    public CreateSavingsAccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountId createSavingsAccount(CreateSavingsAccountCommand command) {
        AccountId accountId = new AccountId(UUID.randomUUID());

        SavingsAccount savingsAccount = SavingsAccount.create(accountId, command.depositCeiling());

        accountRepository.save(savingsAccount);

        return accountId;
    }
}
