package com.boarhat.application.service;

import com.boarhat.application.exception.AccountNotFoundException;
import com.boarhat.application.port.in.GetAccountUseCase;
import com.boarhat.domain.account.Account;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.port.out.AccountRepository;

public class GetAccountService implements GetAccountUseCase {

    private final AccountRepository accountRepository;

    public GetAccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account getAccount(AccountId accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }
}
