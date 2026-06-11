package com.boarhat.domain.port.in.operation;

import com.boarhat.domain.exception.AccountNotFoundException;
import com.boarhat.domain.account.Account;
import com.boarhat.domain.operation.Operation;
import com.boarhat.domain.port.out.AccountRepository;

import java.time.Instant;

class WithdrawService implements WithdrawUseCase {

    private final AccountRepository accountRepository;

    WithdrawService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void withdraw(WithdrawCommand command) {
        Account account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new AccountNotFoundException(command.accountId()));

        Operation operation = account.withdraw(command.amount(), Instant.now());

        accountRepository.save(account, operation);
    }
}
