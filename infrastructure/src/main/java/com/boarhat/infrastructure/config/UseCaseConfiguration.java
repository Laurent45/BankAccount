package com.boarhat.infrastructure.config;

import com.boarhat.application.port.in.CreateBankAccountUseCase;
import com.boarhat.application.port.in.CreateSavingsAccountUseCase;
import com.boarhat.application.port.in.DepositUseCase;
import com.boarhat.application.port.in.GetAccountUseCase;
import com.boarhat.application.port.in.GetStatementUseCase;
import com.boarhat.application.port.in.UpdateOverdraftAuthorizationUseCase;
import com.boarhat.application.port.in.WithdrawUseCase;
import com.boarhat.application.service.CreateBankAccountService;
import com.boarhat.application.service.CreateSavingsAccountService;
import com.boarhat.application.service.DepositService;
import com.boarhat.application.service.GetAccountService;
import com.boarhat.application.service.GetStatementService;
import com.boarhat.application.service.UpdateOverdraftAuthorizationService;
import com.boarhat.application.service.WithdrawService;
import com.boarhat.domain.port.out.AccountRepository;
import com.boarhat.domain.port.out.OperationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class UseCaseConfiguration {

    @Bean
    CreateBankAccountUseCase createBankAccountUseCase(AccountRepository accountRepository) {
        return new CreateBankAccountService(accountRepository);
    }

    @Bean
    CreateSavingsAccountUseCase createSavingsAccountUseCase(AccountRepository accountRepository) {
        return new CreateSavingsAccountService(accountRepository);
    }

    @Bean
    DepositUseCase depositUseCase(AccountRepository accountRepository) {
        return new DepositService(accountRepository);
    }

    @Bean
    WithdrawUseCase withdrawUseCase(AccountRepository accountRepository) {
        return new WithdrawService(accountRepository);
    }

    @Bean
    UpdateOverdraftAuthorizationUseCase updateOverdraftAuthorizationUseCase(AccountRepository accountRepository) {
        return new UpdateOverdraftAuthorizationService(accountRepository);
    }

    @Bean
    GetAccountUseCase getAccountUseCase(AccountRepository accountRepository) {
        return new GetAccountService(accountRepository);
    }

    @Bean
    GetStatementUseCase getStatementUseCase(AccountRepository accountRepository,
                                            OperationRepository operationRepository) {
        return new GetStatementService(accountRepository, operationRepository);
    }
}
