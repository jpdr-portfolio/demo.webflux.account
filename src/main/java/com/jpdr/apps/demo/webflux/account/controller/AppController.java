package com.jpdr.apps.demo.webflux.account.controller;

import com.jpdr.apps.demo.webflux.account.service.AppService;
import com.jpdr.apps.demo.webflux.account.service.dto.account.AccountDto;
import com.jpdr.apps.demo.webflux.account.service.dto.account.AccountTransactionDto;
import com.jpdr.apps.demo.webflux.eventlogger.component.EventLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AppController {
  
  private final AppService appService;
  private final EventLogger eventLogger;
  
  @GetMapping("/accounts/{id}")
  public Mono<ResponseEntity<AccountDto>> findAccountById(@PathVariable(value = "id") Integer id){
    return this.appService.findAccountById(id)
      .doOnNext(account -> this.eventLogger.logEvent("findAccountById", account))
      .map(account-> new ResponseEntity<>(account, HttpStatus.OK));
  }
  
  @GetMapping("/accounts")
  public Mono<ResponseEntity<List<AccountDto>>> findAllAccounts(@RequestParam(value = "ownerId",
    required = false) Integer ownerId){
    return this.appService.findAccounts(ownerId)
      .doOnNext(accounts -> this.eventLogger.logEvent("findAllAccounts", accounts))
      .map(accounts -> new ResponseEntity<>(accounts, HttpStatus.OK));
  }
  
  @PostMapping("/accounts")
  public Mono<ResponseEntity<AccountDto>> createAccount(@RequestBody AccountDto accountDto){
    return this.appService.createAccount(accountDto)
      .doOnNext(account -> this.eventLogger.logEvent("createAccount", account))
      .map(account -> new ResponseEntity<>(account, HttpStatus.CREATED));
  }
  
  @GetMapping("/accounts/transactions")
  public Mono<ResponseEntity<List<AccountTransactionDto>>> findAllTransactions(){
    return this.appService.findAllTransactions()
      .doOnNext(transactions -> this.eventLogger.logEvent("findAllTransactions", transactions))
      .map(transactions -> new ResponseEntity<>(transactions, HttpStatus.OK));
  }
  
  @GetMapping("/accounts/{id}/transactions")
  public Mono<ResponseEntity<List<AccountTransactionDto>>> findTransactions(
    @PathVariable(value = "id", required = false) Integer accountId){
    return this.appService.findTransactions(accountId)
      .doOnNext(transactions -> this.eventLogger.logEvent("findTransactions", transactions))
      .map(transactions -> new ResponseEntity<>(transactions, HttpStatus.OK));
  }
  
  @PostMapping("/accounts/{id}/transactions")
  public  Mono<ResponseEntity<AccountTransactionDto>> createTransaction(
    @PathVariable(value = "id") Integer accountId,
    @RequestBody AccountTransactionDto accountTransactionDto){
    return this.appService.createTransaction(accountId,accountTransactionDto)
      .doOnNext(transaction -> this.eventLogger.logEvent("createTransaction", transaction))
      .map(transaction -> new ResponseEntity<>(transaction, HttpStatus.CREATED));
  }
  
}
