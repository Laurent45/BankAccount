package com.boarhat.infrastructure.adapter.in.web;

import com.boarhat.domain.port.in.account.CreateSavingsAccountCommand;
import com.boarhat.domain.port.in.operation.DepositCommand;
import com.boarhat.domain.port.in.account.UpdateOverdraftAuthorizationCommand;
import com.boarhat.domain.port.in.operation.WithdrawCommand;
import com.boarhat.domain.port.in.account.CreateBankAccountUseCase;
import com.boarhat.domain.port.in.account.CreateSavingsAccountUseCase;
import com.boarhat.domain.port.in.operation.DepositUseCase;
import com.boarhat.domain.port.in.account.GetAccountUseCase;
import com.boarhat.domain.port.in.statement.GetStatementUseCase;
import com.boarhat.domain.port.in.account.UpdateOverdraftAuthorizationUseCase;
import com.boarhat.domain.port.in.operation.WithdrawUseCase;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.account.DepositCeiling;
import com.boarhat.domain.account.OverdraftAuthorization;
import com.boarhat.domain.shared.Amount;
import com.boarhat.infrastructure.adapter.in.web.request.CreateSavingsAccountRequest;
import com.boarhat.infrastructure.adapter.in.web.request.DepositRequest;
import com.boarhat.infrastructure.adapter.in.web.request.UpdateOverdraftAuthorizationRequest;
import com.boarhat.infrastructure.adapter.in.web.request.WithdrawRequest;
import com.boarhat.infrastructure.adapter.in.web.response.AccountCreatedResponse;
import com.boarhat.infrastructure.adapter.in.web.response.AccountResponse;
import com.boarhat.infrastructure.adapter.in.web.response.StatementResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/accounts", version = "1")
class AccountController {

    private final CreateBankAccountUseCase createBankAccountUseCase;
    private final CreateSavingsAccountUseCase createSavingsAccountUseCase;
    private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final UpdateOverdraftAuthorizationUseCase updateOverdraftAuthorizationUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final GetStatementUseCase getStatementUseCase;

    AccountController(CreateBankAccountUseCase createBankAccountUseCase,
                             CreateSavingsAccountUseCase createSavingsAccountUseCase,
                             DepositUseCase depositUseCase,
                             WithdrawUseCase withdrawUseCase,
                             UpdateOverdraftAuthorizationUseCase updateOverdraftAuthorizationUseCase,
                             GetAccountUseCase getAccountUseCase,
                             GetStatementUseCase getStatementUseCase) {
        this.createBankAccountUseCase = createBankAccountUseCase;
        this.createSavingsAccountUseCase = createSavingsAccountUseCase;
        this.depositUseCase = depositUseCase;
        this.withdrawUseCase = withdrawUseCase;
        this.updateOverdraftAuthorizationUseCase = updateOverdraftAuthorizationUseCase;
        this.getAccountUseCase = getAccountUseCase;
        this.getStatementUseCase = getStatementUseCase;
    }

    @PostMapping("/bank")
    public ResponseEntity<AccountCreatedResponse> createBankAccount() {
        AccountId accountId = createBankAccountUseCase.createBankAccount();
        return created(accountId);
    }

    @PostMapping("/savings")
    public ResponseEntity<AccountCreatedResponse> createSavingsAccount(@Valid @RequestBody CreateSavingsAccountRequest request) {
        AccountId accountId = createSavingsAccountUseCase.createSavingsAccount(
                new CreateSavingsAccountCommand(new DepositCeiling(Amount.of(request.depositCeiling()))));
        return created(accountId);
    }

    @PostMapping("/{accountId}/deposits")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deposit(@PathVariable("accountId") UUID accountId, @Valid @RequestBody DepositRequest request) {
        depositUseCase.deposit(new DepositCommand(AccountId.of(accountId), Amount.of(request.amount())));
    }

    @PostMapping("/{accountId}/withdrawals")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdraw(@PathVariable("accountId") UUID accountId, @Valid @RequestBody WithdrawRequest request) {
        withdrawUseCase.withdraw(new WithdrawCommand(AccountId.of(accountId), Amount.of(request.amount())));
    }

    @PutMapping("/{accountId}/overdraft-authorization")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateOverdraftAuthorization(@PathVariable("accountId") UUID accountId,
                                             @Valid @RequestBody UpdateOverdraftAuthorizationRequest request) {
        updateOverdraftAuthorizationUseCase.updateOverdraftAuthorization(new UpdateOverdraftAuthorizationCommand(
                AccountId.of(accountId), new OverdraftAuthorization(request.limit())));
    }

    @GetMapping("/{accountId}")
    public AccountResponse getAccount(@PathVariable("accountId") UUID accountId) {
        return AccountResponse.from(getAccountUseCase.getAccount(AccountId.of(accountId)));
    }

    @GetMapping("/{accountId}/statement")
    public StatementResponse getStatement(@PathVariable("accountId") UUID accountId) {
        return StatementResponse.from(getStatementUseCase.getStatement(AccountId.of(accountId)));
    }

    private static ResponseEntity<AccountCreatedResponse> created(AccountId accountId) {
        URI location = URI.create("/api/accounts/" + accountId.value());
        return ResponseEntity.created(location).body(new AccountCreatedResponse(accountId.value()));
    }
}
