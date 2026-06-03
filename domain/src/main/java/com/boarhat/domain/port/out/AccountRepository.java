package com.boarhat.domain.port.out;

import com.boarhat.domain.model.Account;
import com.boarhat.domain.model.AccountId;

import java.util.Optional;

public interface AccountRepository {
    void save(Account account);
    Optional<Account> findById(AccountId accountId);
}
