package com.boarhat.domain.port.in.operation;

import com.boarhat.domain.exception.AccountNotFoundException;
import com.boarhat.domain.account.Account;
import com.boarhat.domain.operation.Operation;
import com.boarhat.domain.port.out.AccountRepository;

import java.time.Instant;

class DepositService implements DepositUseCase {

    private final AccountRepository accountRepository;

    DepositService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void deposit(DepositCommand command) {
        Account account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new AccountNotFoundException(command.accountId()));

        Operation operation = account.deposit(command.amount(), Instant.now());

        accountRepository.save(account, operation);
    }
}
