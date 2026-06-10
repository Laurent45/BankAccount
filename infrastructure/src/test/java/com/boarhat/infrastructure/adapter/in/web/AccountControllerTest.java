package com.boarhat.infrastructure.adapter.in.web;

import com.boarhat.infrastructure.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Import(TestcontainersConfiguration.class)
class AccountControllerTest {

    @Autowired
    private RestTestClient restTestClient;

    private UUID createBankAccount() {
        AccountCreatedResponse response = restTestClient.post().uri("/accounts/bank")
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AccountCreatedResponse.class)
                .returnResult().getResponseBody();
        return response.accountId();
    }

    @Test
    void should_create_bank_account() {
        assertThat(createBankAccount()).isNotNull();
    }

    @Test
    void should_create_savings_account() {
        AccountCreatedResponse response = restTestClient.post().uri("/accounts/savings")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CreateSavingsAccountRequest(new BigDecimal("1000")))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AccountCreatedResponse.class)
                .returnResult().getResponseBody();

        assertThat(response.accountId()).isNotNull();
    }

    @Test
    void should_deposit_then_show_operation_on_statement() {
        UUID accountId = createBankAccount();

        restTestClient.post().uri("/accounts/{accountId}/deposits", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new DepositRequest(new BigDecimal("150.50")))
                .exchange()
                .expectStatus().isNoContent();

        StatementResponse statement = restTestClient.get().uri("/accounts/{accountId}/statement", accountId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StatementResponse.class)
                .returnResult().getResponseBody();

        assertThat(statement.accountType()).isEqualTo("BANK_ACCOUNT");
        assertThat(statement.balance()).isEqualByComparingTo(new BigDecimal("150.50"));
        assertThat(statement.operations()).hasSize(1);
        assertThat(statement.operations().getFirst().type()).isEqualTo("DEPOSIT");
        assertThat(statement.operations().getFirst().amount()).isEqualByComparingTo(new BigDecimal("150.50"));
    }

    @Test
    void should_withdraw_after_deposit() {
        UUID accountId = createBankAccount();
        restTestClient.post().uri("/accounts/{id}/deposits", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new DepositRequest(new BigDecimal("100")))
                .exchange()
                .expectStatus().isNoContent();

        restTestClient.post().uri("/accounts/{id}/withdrawals", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new WithdrawRequest(new BigDecimal("40")))
                .exchange()
                .expectStatus().isNoContent();

        StatementResponse statement = restTestClient.get().uri("/accounts/{id}/statement", accountId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StatementResponse.class)
                .returnResult().getResponseBody();

        assertThat(statement.balance()).isEqualByComparingTo(new BigDecimal("60"));
    }

    @Test
    void should_return_404_when_account_does_not_exist() {
        restTestClient.get().uri("/accounts/{id}/statement", UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void should_return_422_when_withdrawing_more_than_balance() {
        UUID accountId = createBankAccount();

        restTestClient.post().uri("/accounts/{id}/withdrawals", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new WithdrawRequest(new BigDecimal("50")))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @Test
    void should_return_400_when_depositing_negative_amount() {
        UUID accountId = createBankAccount();

        restTestClient.post().uri("/accounts/{id}/deposits", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new DepositRequest(new BigDecimal("-10")))
                .exchange()
                .expectStatus().isBadRequest();
    }
}
