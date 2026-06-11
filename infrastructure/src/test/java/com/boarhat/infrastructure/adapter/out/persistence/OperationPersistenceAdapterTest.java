package com.boarhat.infrastructure.adapter.out.persistence;

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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class OperationPersistenceAdapterTest {

    private static final Instant NOW = Instant.parse("2026-06-01T12:00:00Z");

    @Autowired
    private AccountPersistenceAdapter accountPersistenceAdapter;

    @Autowired
    private OperationPersistenceAdapter operationPersistenceAdapter;

    @Test
    void should_return_operations_since_given_date_most_recent_first() {
        AccountId accountId = AccountId.of(UUID.randomUUID());
        BankAccount account = BankAccount.create(accountId);
        accountPersistenceAdapter.save(account);

        Operation oldOperation = account.deposit(Amount.of(new BigDecimal("5")), NOW.minus(40, ChronoUnit.DAYS));
        accountPersistenceAdapter.save(account, oldOperation);
        Operation firstRecent = account.deposit(Amount.of(new BigDecimal("100")), NOW.minus(10, ChronoUnit.DAYS));
        accountPersistenceAdapter.save(account, firstRecent);
        Operation lastRecent = account.withdraw(Amount.of(new BigDecimal("30")), NOW.minus(2, ChronoUnit.DAYS));
        accountPersistenceAdapter.save(account, lastRecent);

        List<Operation> operations = operationPersistenceAdapter.findByAccountIdSince(accountId, NOW.minus(30, ChronoUnit.DAYS));

        assertThat(operations).containsExactly(lastRecent, firstRecent);
    }

    @Test
    void should_return_empty_when_account_has_no_operations() {
        AccountId accountId = AccountId.of(UUID.randomUUID());
        accountPersistenceAdapter.save(SavingsAccount.create(accountId,
                new DepositCeiling(Amount.of(new BigDecimal("1000")))));

        assertThat(operationPersistenceAdapter.findByAccountIdSince(accountId, NOW.minus(30, ChronoUnit.DAYS))).isEmpty();
    }

    @Test
    void should_not_return_operations_of_other_accounts() {
        AccountId accountId = AccountId.of(UUID.randomUUID());
        AccountId otherAccountId = AccountId.of(UUID.randomUUID());
        BankAccount account = BankAccount.reconstruct(accountId, Balance.zero(), OverdraftAuthorization.notAllowed());
        BankAccount otherAccount = BankAccount.reconstruct(otherAccountId, Balance.zero(), OverdraftAuthorization.notAllowed());
        accountPersistenceAdapter.save(account);
        accountPersistenceAdapter.save(otherAccount);

        accountPersistenceAdapter.save(account, account.deposit(Amount.of(new BigDecimal("10")), NOW.minus(1, ChronoUnit.DAYS)));
        Operation otherOperation = otherAccount.deposit(Amount.of(new BigDecimal("20")), NOW.minus(1, ChronoUnit.DAYS));
        accountPersistenceAdapter.save(otherAccount, otherOperation);

        assertThat(operationPersistenceAdapter.findByAccountIdSince(otherAccountId, NOW.minus(30, ChronoUnit.DAYS)))
                .containsExactly(otherOperation);
    }
}
