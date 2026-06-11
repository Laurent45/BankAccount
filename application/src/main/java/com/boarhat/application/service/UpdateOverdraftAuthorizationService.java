package com.boarhat.application.service;

import com.boarhat.application.command.UpdateOverdraftAuthorizationCommand;
import com.boarhat.application.exception.AccountNotFoundException;
import com.boarhat.application.exception.OverdraftNotSupportedException;
import com.boarhat.application.port.in.UpdateOverdraftAuthorizationUseCase;
import com.boarhat.domain.account.Account;
import com.boarhat.domain.account.BankAccount;
import com.boarhat.domain.port.out.AccountRepository;

public class UpdateOverdraftAuthorizationService implements UpdateOverdraftAuthorizationUseCase {

    private final AccountRepository accountRepository;

    public UpdateOverdraftAuthorizationService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void updateOverdraftAuthorization(UpdateOverdraftAuthorizationCommand command) {
        Account account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new AccountNotFoundException(command.accountId()));

        if (!(account instanceof BankAccount bankAccount)) {
            throw new OverdraftNotSupportedException(command.accountId());
        }

        bankAccount.allowOverdraft(command.overdraftAuthorization());

        accountRepository.save(bankAccount);
    }
}
