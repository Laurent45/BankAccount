package com.boarhat.application.fake;

import com.boarhat.domain.account.Account;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.port.out.AccountRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryAccountRepository implements AccountRepository {

    Map<AccountId, Account> accounts = new HashMap<>();

    @Override
    public void save(Account account) {
        accounts.put(account.getAccountId(), account);
    }

    @Override
    public Optional<Account> findById(AccountId accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }

    public Account getOrThrow(AccountId accountId) {
        return Optional.ofNullable(accounts.get(accountId)).orElseThrow();
    }
}
