package com.boarhat.application.service;

import com.boarhat.application.command.UpdateOverdraftAuthorizationCommand;
import com.boarhat.application.exception.AccountNotFoundException;
import com.boarhat.application.exception.OverdraftNotSupportedException;
import com.boarhat.application.fake.InMemoryAccountRepository;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.account.BankAccount;
import com.boarhat.domain.account.DepositCeiling;
import com.boarhat.domain.account.OverdraftAuthorization;
import com.boarhat.domain.account.SavingsAccount;
import com.boarhat.domain.shared.Amount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UpdateOverdraftAuthorizationServiceTest {
    private static final AccountId ACCOUNT_ID = new AccountId(UUID.randomUUID());

    private InMemoryAccountRepository accountRepository;
    private UpdateOverdraftAuthorizationService updateOverdraftAuthorizationService;

    @BeforeEach
    void setUp() {
        accountRepository = new InMemoryAccountRepository();
        updateOverdraftAuthorizationService = new UpdateOverdraftAuthorizationService(accountRepository);
    }

    @Test
    void should_update_overdraft_authorization_of_bank_account() {
        accountRepository.save(BankAccount.create(ACCOUNT_ID));
        OverdraftAuthorization authorization = OverdraftAuthorization.allowed(Amount.of(new BigDecimal("200")));

        updateOverdraftAuthorizationService.updateOverdraftAuthorization(
                new UpdateOverdraftAuthorizationCommand(ACCOUNT_ID, authorization));

        BankAccount saved = (BankAccount) accountRepository.getOrThrow(ACCOUNT_ID);
        assertThat(saved.getOverdraftAuthorization()).isEqualTo(authorization);
    }

    @Test
    void should_remove_overdraft_when_not_allowed() {
        BankAccount account = BankAccount.create(ACCOUNT_ID);
        account.allowOverdraft(OverdraftAuthorization.allowed(Amount.of(new BigDecimal("500"))));
        accountRepository.save(account);

        updateOverdraftAuthorizationService.updateOverdraftAuthorization(
                new UpdateOverdraftAuthorizationCommand(ACCOUNT_ID, OverdraftAuthorization.notAllowed()));

        BankAccount saved = (BankAccount) accountRepository.getOrThrow(ACCOUNT_ID);
        assertThat(saved.getOverdraftAuthorization()).isEqualTo(OverdraftAuthorization.notAllowed());
    }

    @Test
    void should_throw_when_account_not_found() {
        UpdateOverdraftAuthorizationCommand command = new UpdateOverdraftAuthorizationCommand(
                ACCOUNT_ID, OverdraftAuthorization.notAllowed());

        assertThatThrownBy(() -> updateOverdraftAuthorizationService.updateOverdraftAuthorization(command))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void should_throw_when_account_is_a_savings_account() {
        accountRepository.save(SavingsAccount.create(ACCOUNT_ID,
                new DepositCeiling(Amount.of(new BigDecimal("1000")))));
        UpdateOverdraftAuthorizationCommand command = new UpdateOverdraftAuthorizationCommand(
                ACCOUNT_ID, OverdraftAuthorization.allowed(Amount.of(new BigDecimal("200"))));

        assertThatThrownBy(() -> updateOverdraftAuthorizationService.updateOverdraftAuthorization(command))
                .isInstanceOf(OverdraftNotSupportedException.class);
    }
}
