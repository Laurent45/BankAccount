package com.boarhat.domain.port.in.account;

import com.boarhat.domain.exception.AccountNotFoundException;
import com.boarhat.domain.exception.OverdraftNotSupportedException;
import com.boarhat.domain.account.Account;
import com.boarhat.domain.account.BankAccount;
import com.boarhat.domain.port.out.AccountRepository;

class UpdateOverdraftAuthorizationService implements UpdateOverdraftAuthorizationUseCase {

    private final AccountRepository accountRepository;

    UpdateOverdraftAuthorizationService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void updateOverdraftAuthorization(UpdateOverdraftAuthorizationCommand command) {
        Account account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new AccountNotFoundException(command.accountId()));

        if (!(account instanceof BankAccount bankAccount)) {
            throw new OverdraftNotSupportedException(command.accountId());
        }

        bankAccount.updateOverdraftAuthorization(command.overdraftAuthorization());

        accountRepository.save(bankAccount);
    }
}
