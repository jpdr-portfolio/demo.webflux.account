package com.jpdr.apps.demo.webflux.account.service;

import com.jpdr.apps.demo.webflux.account.service.dto.account.AccountDto;
import com.jpdr.apps.demo.webflux.account.service.dto.account.AccountTransactionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AppService {
  
  Flux<AccountDto> findAccounts(Integer ownerId);
  Flux<AccountDto> findAllAccounts();
  Flux<AccountDto> findAllAccountsByOwnerId(Integer ownerId);
  
  Mono<AccountDto> findAccountById(Integer accountId);
  
  Mono<AccountDto> createAccount(AccountDto accountDto);
  
  Flux<AccountTransactionDto> findAllTransactions();
  
  Mono<AccountTransactionDto> createTransaction(Integer accountId, AccountTransactionDto accountTransactionDto);
  
  Flux<AccountTransactionDto> findTransactions(Integer accountId);
  Flux<AccountTransactionDto> findTransactionsByAccountId(Integer accountId);
  
}
