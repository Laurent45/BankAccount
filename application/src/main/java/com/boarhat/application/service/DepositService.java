package com.boarhat.application.service;

import com.boarhat.application.command.DepositCommand;
import com.boarhat.application.exception.AccountNotFoundException;
import com.boarhat.application.port.in.DepositUseCase;
import com.boarhat.domain.account.Account;
import com.boarhat.domain.port.out.AccountRepository;

import java.time.LocalDateTime;

public class DepositService implements DepositUseCase {

    private final AccountRepository accountRepository;

    public DepositService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void deposit(DepositCommand command) {
        Account account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new AccountNotFoundException(command.accountId()));

        account.deposit(command.amount(), LocalDateTime.now());

        accountRepository.save(account);
    }
}
