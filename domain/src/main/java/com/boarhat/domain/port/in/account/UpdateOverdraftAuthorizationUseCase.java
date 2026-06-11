package com.boarhat.domain.port.in.account;

import com.boarhat.domain.port.out.AccountRepository;

public interface UpdateOverdraftAuthorizationUseCase {
    void updateOverdraftAuthorization(UpdateOverdraftAuthorizationCommand command);

    static UpdateOverdraftAuthorizationUseCase create(AccountRepository accountRepository) {
        return new UpdateOverdraftAuthorizationService(accountRepository);
    }
}
