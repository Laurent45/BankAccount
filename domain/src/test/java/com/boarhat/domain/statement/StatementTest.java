package com.boarhat.domain.statement;

import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.account.AccountType;
import com.boarhat.domain.account.BankAccount;
import com.boarhat.domain.account.DepositCeiling;
import com.boarhat.domain.account.OverdraftAuthorization;
import com.boarhat.domain.account.SavingsAccount;
import com.boarhat.domain.operation.Operation;
import com.boarhat.domain.operation.OperationType;
import com.boarhat.domain.shared.Amount;
import com.boarhat.domain.shared.Balance;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StatementTest {

    private static final AccountId ACCOUNT_ID = new AccountId(UUID.randomUUID());
    private static final OverdraftAuthorization NO_OVERDRAFT = OverdraftAuthorization.notAllowed();
    private static final DepositCeiling CEILING = new DepositCeiling(Amount.of(new BigDecimal("10000")));

    @Nested
    class Type {

        @Test
        void should_return_bank_account_type() {
            BankAccount account = BankAccount.create(ACCOUNT_ID);

            assertThat(account.getStatement().accountType()).isEqualTo(AccountType.BANK_ACCOUNT);
        }

        @Test
        void should_return_savings_account_type() {
            SavingsAccount account = SavingsAccount.create(ACCOUNT_ID, CEILING);

            assertThat(account.getStatement().accountType()).isEqualTo(AccountType.SAVINGS_ACCOUNT);
        }
    }

    @Nested
    class CurrentBalance {

        @Test
        void should_return_current_balance_in_statement() {
            BankAccount account = BankAccount.reconstruct(ACCOUNT_ID, Balance.of(new BigDecimal("500")), NO_OVERDRAFT, List.of());
            account.deposit(Amount.of(new BigDecimal("200")));

            assertThat(account.getStatement().balance()).isEqualTo(Balance.of(new BigDecimal("700")));
        }
    }

    @Nested
    class Sorting {

        @Test
        void should_sort_operations_in_reverse_chronological_order() {
            LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
            LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

            List<Operation> operations = List.of(
                    new Operation(OperationType.DEPOSIT, Amount.of(new BigDecimal("100")), Balance.of(new BigDecimal("100")), twoDaysAgo),
                    new Operation(OperationType.WITHDRAW, Amount.of(new BigDecimal("50")), Balance.of(new BigDecimal("50")), yesterday),
                    new Operation(OperationType.DEPOSIT, Amount.of(new BigDecimal("200")), Balance.of(new BigDecimal("250")), oneHourAgo)
            );

            BankAccount account = BankAccount.reconstruct(ACCOUNT_ID, Balance.of(new BigDecimal("250")), NO_OVERDRAFT, operations);

            Statement statement = account.getStatement();

            assertThat(statement.operations()).hasSize(3);
            assertThat(statement.operations().get(0).occurredAt()).isEqualTo(oneHourAgo);
            assertThat(statement.operations().get(1).occurredAt()).isEqualTo(yesterday);
            assertThat(statement.operations().get(2).occurredAt()).isEqualTo(twoDaysAgo);
        }
    }

    @Nested
    class RollingMonthFilter {

        @Test
        void should_exclude_operations_older_than_one_month() {
            List<Operation> operations = List.of(
                    new Operation(OperationType.DEPOSIT, Amount.of(new BigDecimal("100")), Balance.of(new BigDecimal("100")), LocalDateTime.now().minusMonths(2)),
                    new Operation(OperationType.DEPOSIT, Amount.of(new BigDecimal("200")), Balance.of(new BigDecimal("300")), LocalDateTime.now().minusDays(7))
            );

            BankAccount account = BankAccount.reconstruct(ACCOUNT_ID, Balance.of(new BigDecimal("300")), NO_OVERDRAFT, operations);

            Statement statement = account.getStatement();

            assertThat(statement.operations()).hasSize(1);
            assertThat(statement.operations().getFirst().amount()).isEqualTo(Amount.of(new BigDecimal("200")));
        }

        @Test
        void should_return_empty_when_all_operations_older_than_one_month() {
            List<Operation> operations = List.of(
                    new Operation(OperationType.DEPOSIT, Amount.of(new BigDecimal("100")), Balance.of(new BigDecimal("100")), LocalDateTime.now().minusMonths(2))
            );

            BankAccount account = BankAccount.reconstruct(ACCOUNT_ID, Balance.of(new BigDecimal("100")), NO_OVERDRAFT, operations);

            assertThat(account.getStatement().operations()).isEmpty();
        }
    }
}
