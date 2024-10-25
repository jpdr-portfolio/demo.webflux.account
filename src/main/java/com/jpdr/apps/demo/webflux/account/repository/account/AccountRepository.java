package com.jpdr.apps.demo.webflux.account.repository.account;

import com.jpdr.apps.demo.webflux.account.model.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveCrudRepository<Account, Integer> {
  
  Flux<Account> findAllByIsActiveIsTrue();
  Mono<Account> findByIdAndIsActiveIsTrue(int id);
  Flux<Account> findByOwnerIdAndIsActiveIsTrue(int userId);
  
}
