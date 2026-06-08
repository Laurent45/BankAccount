package com.boarhat.application.service;

import com.boarhat.application.port.in.CreateBankAccountUseCase;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.account.BankAccount;
import com.boarhat.domain.port.out.AccountRepository;

import java.util.UUID;

public class CreateBankAccountService implements CreateBankAccountUseCase {
    private final AccountRepository accountRepository;

    public CreateBankAccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountId createBankAccount() {
        AccountId accountId = new AccountId(UUID.randomUUID());

        BankAccount bankAccount = BankAccount.create(accountId);

        accountRepository.save(bankAccount);

        return accountId;
    }
}
