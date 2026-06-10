package com.boarhat.domain.port.out;

import com.boarhat.domain.account.Account;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.operation.Operation;

import java.util.Optional;

public interface AccountRepository {

    void save(Account account);

    /**
     * Persists the account together with the operation that explains its new balance,
     * atomically: either both are stored or neither is.
     */
    void save(Account account, Operation operation);

    Optional<Account> findById(AccountId accountId);
}
