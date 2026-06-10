package com.boarhat.infrastructure.adapter.out.persistence;

import com.boarhat.domain.account.Account;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.account.BankAccount;
import com.boarhat.domain.account.DepositCeiling;
import com.boarhat.domain.account.OverdraftAuthorization;
import com.boarhat.domain.account.SavingsAccount;
import com.boarhat.domain.operation.Operation;
import com.boarhat.domain.shared.Amount;
import com.boarhat.domain.shared.Balance;
import com.boarhat.infrastructure.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class AccountPersistenceAdapterTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 6, 1, 12, 0);

    @Autowired
    private AccountPersistenceAdapter accountPersistenceAdapter;

    @Test
    void should_save_and_reload_bank_account() {
        AccountId accountId = AccountId.of(UUID.randomUUID());
        BankAccount account = BankAccount.reconstruct(accountId, Balance.of(new BigDecimal("100.50")),
                OverdraftAuthorization.allowed(Amount.of(new BigDecimal("50"))));

        accountPersistenceAdapter.save(account);

        Account reloaded = accountPersistenceAdapter.findById(accountId).orElseThrow();
        assertThat(reloaded).isInstanceOf(BankAccount.class);
        assertThat(reloaded.getBalance()).isEqualTo(Balance.of(new BigDecimal("100.50")));
        assertThat(((BankAccount) reloaded).getOverdraftAuthorization())
                .isEqualTo(OverdraftAuthorization.allowed(Amount.of(new BigDecimal("50"))));
    }

    @Test
    void should_save_and_reload_savings_account() {
        AccountId accountId = AccountId.of(UUID.randomUUID());
        SavingsAccount account = SavingsAccount.reconstruct(accountId, Balance.of(new BigDecimal("200")),
                new DepositCeiling(Amount.of(new BigDecimal("1000"))));

        accountPersistenceAdapter.save(account);

        Account reloaded = accountPersistenceAdapter.findById(accountId).orElseThrow();
        assertThat(reloaded).isInstanceOf(SavingsAccount.class);
        assertThat(reloaded.getBalance()).isEqualTo(Balance.of(new BigDecimal("200")));
        assertThat(((SavingsAccount) reloaded).getDepositCeiling())
                .isEqualTo(new DepositCeiling(Amount.of(new BigDecimal("1000"))));
    }

    @Test
    void should_return_empty_when_account_does_not_exist() {
        assertThat(accountPersistenceAdapter.findById(AccountId.of(UUID.randomUUID()))).isEmpty();
    }

    @Test
    void should_update_balance_when_saving_account_with_operation() {
        AccountId accountId = AccountId.of(UUID.randomUUID());
        BankAccount account = BankAccount.create(accountId);
        accountPersistenceAdapter.save(account);

        Operation operation = account.deposit(Amount.of(new BigDecimal("75.25")), NOW);
        accountPersistenceAdapter.save(account, operation);

        Account reloaded = accountPersistenceAdapter.findById(accountId).orElseThrow();
        assertThat(reloaded.getBalance()).isEqualTo(Balance.of(new BigDecimal("75.25")));
    }
}
