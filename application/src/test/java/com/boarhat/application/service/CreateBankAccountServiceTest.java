package com.boarhat.application.service;

import com.boarhat.application.fake.InMemoryAccountRepository;
import com.boarhat.domain.account.Account;
import com.boarhat.domain.account.AccountId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateBankAccountServiceTest {
    private InMemoryAccountRepository repository;
    private CreateBankAccountService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAccountRepository();
        service = new CreateBankAccountService(repository);
    }

    @Test
    void should_save_account_and_return_its_id() {
        AccountId accountId = service.createBankAccount();

        Account saved = repository.getOrThrow(accountId);
        assertThat(saved.getAccountId()).isEqualTo(accountId);
    }

    @Test
    void should_generate_unique_id_for_each_account() {
        AccountId first = service.createBankAccount();
        AccountId second = service.createBankAccount();

        assertThat(first).isNotEqualTo(second);
    }

}