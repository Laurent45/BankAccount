package com.boarhat.application.service;

import com.boarhat.application.command.WithdrawCommand;
import com.boarhat.application.exception.AccountNotFoundException;
import com.boarhat.application.port.in.WithdrawUseCase;
import com.boarhat.domain.account.Account;
import com.boarhat.domain.port.out.AccountRepository;

import java.time.LocalDateTime;

public class WithdrawService implements WithdrawUseCase {

    private final AccountRepository accountRepository;

    public WithdrawService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void withdraw(WithdrawCommand command) {
        Account account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new AccountNotFoundException(command.accountId()));

        account.withdraw(command.amount(), LocalDateTime.now());

        accountRepository.save(account);
    }
}
