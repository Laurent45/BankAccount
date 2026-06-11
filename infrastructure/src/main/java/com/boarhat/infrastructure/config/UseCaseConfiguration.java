package com.boarhat.infrastructure.config;

import com.boarhat.domain.port.in.account.CreateBankAccountUseCase;
import com.boarhat.domain.port.in.account.CreateSavingsAccountUseCase;
import com.boarhat.domain.port.in.operation.DepositUseCase;
import com.boarhat.domain.port.in.account.GetAccountUseCase;
import com.boarhat.domain.port.in.statement.GetStatementUseCase;
import com.boarhat.domain.port.in.account.UpdateOverdraftAuthorizationUseCase;
import com.boarhat.domain.port.in.operation.WithdrawUseCase;
import com.boarhat.domain.port.out.AccountRepository;
import com.boarhat.domain.port.out.OperationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class UseCaseConfiguration {

    @Bean
    CreateBankAccountUseCase createBankAccountUseCase(AccountRepository accountRepository) {
        return CreateBankAccountUseCase.create(accountRepository);
    }

    @Bean
    CreateSavingsAccountUseCase createSavingsAccountUseCase(AccountRepository accountRepository) {
        return CreateSavingsAccountUseCase.create(accountRepository);
    }

    @Bean
    DepositUseCase depositUseCase(AccountRepository accountRepository) {
        return DepositUseCase.create(accountRepository);
    }

    @Bean
    WithdrawUseCase withdrawUseCase(AccountRepository accountRepository) {
        return WithdrawUseCase.create(accountRepository);
    }

    @Bean
    UpdateOverdraftAuthorizationUseCase updateOverdraftAuthorizationUseCase(AccountRepository accountRepository) {
        return UpdateOverdraftAuthorizationUseCase.create(accountRepository);
    }

    @Bean
    GetAccountUseCase getAccountUseCase(AccountRepository accountRepository) {
        return GetAccountUseCase.create(accountRepository);
    }

    @Bean
    GetStatementUseCase getStatementUseCase(AccountRepository accountRepository,
                                            OperationRepository operationRepository) {
        return GetStatementUseCase.create(accountRepository, operationRepository);
    }
}
