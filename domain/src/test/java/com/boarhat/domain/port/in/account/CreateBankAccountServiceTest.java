package com.boarhat.domain.port.in.account;

import com.boarhat.domain.fake.InMemoryAccountRepository;
import com.boarhat.domain.account.Account;
import com.boarhat.domain.account.AccountId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateBankAccountServiceTest {
    private InMemoryAccountRepository repository;
    private CreateBankAccountUseCase service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAccountRepository();
        service = CreateBankAccountUseCase.create(repository);
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