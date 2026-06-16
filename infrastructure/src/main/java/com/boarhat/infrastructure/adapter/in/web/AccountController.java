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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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
@Tag(name = "Accounts", description = "Create accounts, move money and consult balances and statements")
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

    @Operation(summary = "Create a bank account",
            description = "Creates a new bank account with a zero balance and no overdraft authorization.")
    @ApiResponse(responseCode = "201", description = "Account created")
    @PostMapping("/bank")
    public ResponseEntity<AccountCreatedResponse> createBankAccount() {
        AccountId accountId = createBankAccountUseCase.createBankAccount();
        return created(accountId);
    }

    @Operation(summary = "Create a savings account",
            description = "Creates a new savings account with a zero balance and the given deposit ceiling.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/savings")
    public ResponseEntity<AccountCreatedResponse> createSavingsAccount(@Valid @RequestBody CreateSavingsAccountRequest request) {
        AccountId accountId = createSavingsAccountUseCase.createSavingsAccount(
                new CreateSavingsAccountCommand(new DepositCeiling(Amount.of(request.depositCeiling()))));
        return created(accountId);
    }

    @Operation(summary = "Deposit money",
            description = "Deposits the given amount on the account.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Money deposited"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Deposit ceiling reached",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{accountId}/deposits")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deposit(@Parameter(description = "Account identifier") @PathVariable("accountId") UUID accountId,
                        @Valid @RequestBody DepositRequest request) {
        depositUseCase.deposit(new DepositCommand(AccountId.of(accountId), Amount.of(request.amount())));
    }

    @Operation(summary = "Withdraw money",
            description = "Withdraws the given amount from the account, within the overdraft authorization.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Money withdrawn"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Insufficient funds",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{accountId}/withdrawals")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdraw(@Parameter(description = "Account identifier") @PathVariable("accountId") UUID accountId,
                         @Valid @RequestBody WithdrawRequest request) {
        withdrawUseCase.withdraw(new WithdrawCommand(AccountId.of(accountId), Amount.of(request.amount())));
    }

    @Operation(summary = "Update overdraft authorization",
            description = "Sets the overdraft authorization limit of a bank account.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Overdraft authorization updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Overdraft not supported by the account",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{accountId}/overdraft-authorization")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateOverdraftAuthorization(@Parameter(description = "Account identifier") @PathVariable("accountId") UUID accountId,
                                             @Valid @RequestBody UpdateOverdraftAuthorizationRequest request) {
        updateOverdraftAuthorizationUseCase.updateOverdraftAuthorization(new UpdateOverdraftAuthorizationCommand(
                AccountId.of(accountId), new OverdraftAuthorization(request.limit())));
    }

    @Operation(summary = "Get an account",
            description = "Returns the account details including its current balance.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{accountId}")
    public AccountResponse getAccount(@Parameter(description = "Account identifier") @PathVariable("accountId") UUID accountId) {
        return AccountResponse.from(getAccountUseCase.getAccount(AccountId.of(accountId)));
    }

    @Operation(summary = "Get an account statement",
            description = "Returns the account statement with the full list of operations.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statement issued"),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{accountId}/statement")
    public StatementResponse getStatement(@Parameter(description = "Account identifier") @PathVariable("accountId") UUID accountId) {
        return StatementResponse.from(getStatementUseCase.getStatement(AccountId.of(accountId)));
    }

    private static ResponseEntity<AccountCreatedResponse> created(AccountId accountId) {
        URI location = URI.create("/api/accounts/" + accountId.value());
        return ResponseEntity.created(location).body(new AccountCreatedResponse(accountId.value()));
    }
}
