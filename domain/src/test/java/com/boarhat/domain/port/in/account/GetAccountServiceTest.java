package com.boarhat.domain.port.in.account;

import com.boarhat.domain.exception.AccountNotFoundException;
import com.boarhat.domain.fake.InMemoryAccountRepository;
import com.boarhat.domain.account.Account;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.account.BankAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetAccountServiceTest {
    private static final AccountId ACCOUNT_ID = new AccountId(UUID.randomUUID());

    private InMemoryAccountRepository accountRepository;
    private GetAccountUseCase getAccountService;

    @BeforeEach
    void setUp() {
        accountRepository = new InMemoryAccountRepository();
        getAccountService = GetAccountUseCase.create(accountRepository);
    }

    @Test
    void should_return_account_when_it_exists() {
        accountRepository.save(BankAccount.create(ACCOUNT_ID));

        Account account = getAccountService.getAccount(ACCOUNT_ID);

        assertThat(account.getAccountId()).isEqualTo(ACCOUNT_ID);
    }

    @Test
    void should_throw_when_account_not_found() {
        assertThatThrownBy(() -> getAccountService.getAccount(ACCOUNT_ID))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
