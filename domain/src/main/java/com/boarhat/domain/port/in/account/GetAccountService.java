package com.boarhat.domain.port.in.account;

import com.boarhat.domain.exception.AccountNotFoundException;
import com.boarhat.domain.account.Account;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.port.out.AccountRepository;

class GetAccountService implements GetAccountUseCase {

    private final AccountRepository accountRepository;

    GetAccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account getAccount(AccountId accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }
}
