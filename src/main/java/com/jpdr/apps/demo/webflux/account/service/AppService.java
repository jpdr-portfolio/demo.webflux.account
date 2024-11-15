package com.jpdr.apps.demo.webflux.account.service;

import com.jpdr.apps.demo.webflux.account.service.dto.account.AccountDto;
import com.jpdr.apps.demo.webflux.account.service.dto.account.AccountTransactionDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AppService {
  
  Mono<List<AccountDto>> findAccounts(Integer ownerId);
  Mono<List<AccountDto>> findAllAccounts();
  Mono<List<AccountDto>> findAllAccountsByOwnerId(Integer ownerId);
  
  Mono<AccountDto> findAccountById(Integer accountId);
  
  Mono<AccountDto> createAccount(AccountDto accountDto);
  
  Mono<List<AccountTransactionDto>> findAllTransactions();
  
  Mono<AccountTransactionDto> createTransaction(Integer accountId, AccountTransactionDto accountTransactionDto);
  
  Mono<List<AccountTransactionDto>> findTransactions(Integer accountId);
  Mono<List<AccountTransactionDto>> findTransactionsByAccountId(Integer accountId);
  
}
