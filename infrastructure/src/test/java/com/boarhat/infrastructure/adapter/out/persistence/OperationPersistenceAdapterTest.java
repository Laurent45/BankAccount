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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class OperationPersistenceAdapterTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 6, 1, 12, 0);

    @Autowired
    private AccountPersistenceAdapter accountPersistenceAdapter;

    @Autowired
    private OperationPersistenceAdapter operationPersistenceAdapter;

    @Test
    void should_return_operations_since_given_date_most_recent_first() {
        AccountId accountId = AccountId.of(UUID.randomUUID());
        BankAccount account = BankAccount.create(accountId);
        accountPersistenceAdapter.save(account);

        Operation oldOperation = account.deposit(Amount.of(new BigDecimal("5")), NOW.minusDays(40));
        accountPersistenceAdapter.save(account, oldOperation);
        Operation firstRecent = account.deposit(Amount.of(new BigDecimal("100")), NOW.minusDays(10));
        accountPersistenceAdapter.save(account, firstRecent);
        Operation lastRecent = account.withdraw(Amount.of(new BigDecimal("30")), NOW.minusDays(2));
        accountPersistenceAdapter.save(account, lastRecent);

        List<Operation> operations = operationPersistenceAdapter.findByAccountIdSince(accountId, NOW.minusMonths(1));

        assertThat(operations).containsExactly(lastRecent, firstRecent);
    }

    @Test
    void should_return_empty_when_account_has_no_operations() {
        AccountId accountId = AccountId.of(UUID.randomUUID());
        accountPersistenceAdapter.save(SavingsAccount.create(accountId,
                new DepositCeiling(Amount.of(new BigDecimal("1000")))));

        assertThat(operationPersistenceAdapter.findByAccountIdSince(accountId, NOW.minusMonths(1))).isEmpty();
    }

    @Test
    void should_not_return_operations_of_other_accounts() {
        AccountId accountId = AccountId.of(UUID.randomUUID());
        AccountId otherAccountId = AccountId.of(UUID.randomUUID());
        BankAccount account = BankAccount.reconstruct(accountId, Balance.zero(), OverdraftAuthorization.notAllowed());
        BankAccount otherAccount = BankAccount.reconstruct(otherAccountId, Balance.zero(), OverdraftAuthorization.notAllowed());
        accountPersistenceAdapter.save(account);
        accountPersistenceAdapter.save(otherAccount);

        accountPersistenceAdapter.save(account, account.deposit(Amount.of(new BigDecimal("10")), NOW.minusDays(1)));
        Operation otherOperation = otherAccount.deposit(Amount.of(new BigDecimal("20")), NOW.minusDays(1));
        accountPersistenceAdapter.save(otherAccount, otherOperation);

        assertThat(operationPersistenceAdapter.findByAccountIdSince(otherAccountId, NOW.minusMonths(1)))
                .containsExactly(otherOperation);
    }
}
