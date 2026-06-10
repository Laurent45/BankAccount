package com.boarhat.infrastructure.adapter.in.web;

import com.boarhat.application.command.CreateSavingsAccountCommand;
import com.boarhat.application.command.DepositCommand;
import com.boarhat.application.command.WithdrawCommand;
import com.boarhat.application.port.in.CreateBankAccountUseCase;
import com.boarhat.application.port.in.CreateSavingsAccountUseCase;
import com.boarhat.application.port.in.DepositUseCase;
import com.boarhat.application.port.in.GetStatementUseCase;
import com.boarhat.application.port.in.WithdrawUseCase;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.account.DepositCeiling;
import com.boarhat.domain.shared.Amount;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
class AccountController {

    private final CreateBankAccountUseCase createBankAccountUseCase;
    private final CreateSavingsAccountUseCase createSavingsAccountUseCase;
    private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final GetStatementUseCase getStatementUseCase;

    AccountController(CreateBankAccountUseCase createBankAccountUseCase,
                             CreateSavingsAccountUseCase createSavingsAccountUseCase,
                             DepositUseCase depositUseCase,
                             WithdrawUseCase withdrawUseCase,
                             GetStatementUseCase getStatementUseCase) {
        this.createBankAccountUseCase = createBankAccountUseCase;
        this.createSavingsAccountUseCase = createSavingsAccountUseCase;
        this.depositUseCase = depositUseCase;
        this.withdrawUseCase = withdrawUseCase;
        this.getStatementUseCase = getStatementUseCase;
    }

    @PostMapping("/bank")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountCreatedResponse createBankAccount() {
        AccountId accountId = createBankAccountUseCase.createBankAccount();
        return new AccountCreatedResponse(accountId.value());
    }

    @PostMapping("/savings")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountCreatedResponse createSavingsAccount(@RequestBody CreateSavingsAccountRequest request) {
        AccountId accountId = createSavingsAccountUseCase.createSavingsAccount(
                new CreateSavingsAccountCommand(new DepositCeiling(Amount.of(request.depositCeiling()))));
        return new AccountCreatedResponse(accountId.value());
    }

    @PostMapping("/{accountId}/deposits")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deposit(@PathVariable("accountId") UUID accountId, @RequestBody DepositRequest request) {
        depositUseCase.deposit(new DepositCommand(AccountId.of(accountId), Amount.of(request.amount())));
    }

    @PostMapping("/{accountId}/withdrawals")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdraw(@PathVariable("accountId") UUID accountId, @RequestBody WithdrawRequest request) {
        withdrawUseCase.withdraw(new WithdrawCommand(AccountId.of(accountId), Amount.of(request.amount())));
    }

    @GetMapping("/{accountId}/statement")
    public StatementResponse getStatement(@PathVariable("accountId") UUID accountId) {
        return StatementResponse.from(getStatementUseCase.getStatement(AccountId.of(accountId)));
    }
}
