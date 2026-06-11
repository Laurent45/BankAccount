package com.boarhat.infrastructure.adapter.out.persistence;

import com.boarhat.domain.account.Account;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.operation.Operation;
import com.boarhat.domain.port.out.AccountRepository;
import com.boarhat.infrastructure.adapter.out.persistence.mapper.AccountMapper;
import com.boarhat.infrastructure.adapter.out.persistence.mapper.OperationMapper;
import com.boarhat.infrastructure.adapter.out.persistence.repository.AccountJpaRepository;
import com.boarhat.infrastructure.adapter.out.persistence.repository.OperationJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
class AccountPersistenceAdapter implements AccountRepository {

    private final AccountJpaRepository accountJpaRepository;
    private final OperationJpaRepository operationJpaRepository;

    public AccountPersistenceAdapter(AccountJpaRepository accountJpaRepository,
                                     OperationJpaRepository operationJpaRepository) {
        this.accountJpaRepository = accountJpaRepository;
        this.operationJpaRepository = operationJpaRepository;
    }

    @Override
    public void save(Account account) {
        accountJpaRepository.save(AccountMapper.toEntity(account));
    }

    @Override
    @Transactional
    public void save(Account account, Operation operation) {
        accountJpaRepository.save(AccountMapper.toEntity(account));
        operationJpaRepository.save(OperationMapper.toEntity(account.getAccountId(), operation));
    }

    @Override
    public Optional<Account> findById(AccountId accountId) {
        return accountJpaRepository.findById(accountId.value())
                .map(AccountMapper::toDomain);
    }
}
