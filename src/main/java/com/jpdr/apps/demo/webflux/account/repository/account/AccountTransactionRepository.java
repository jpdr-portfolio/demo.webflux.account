package com.jpdr.apps.demo.webflux.account.repository.account;

import com.jpdr.apps.demo.webflux.account.model.Account;
import com.jpdr.apps.demo.webflux.account.model.AccountTransaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AccountTransactionRepository extends ReactiveCrudRepository<AccountTransaction, Integer> {
  
  Flux<AccountTransaction> findByAccountIdOrderByTransactionDateDesc(Integer accountId);
  
}
