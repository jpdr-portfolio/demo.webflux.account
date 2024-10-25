package com.jpdr.apps.demo.webflux.account.controller;

import com.jpdr.apps.demo.webflux.account.service.AppService;
import com.jpdr.apps.demo.webflux.account.service.dto.account.AccountDto;
import com.jpdr.apps.demo.webflux.account.service.dto.account.AccountTransactionDto;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AppController {
  
  private final AppService appService;
  
  @GetMapping("/accounts/{id}")
  public ResponseEntity<Mono<AccountDto>> findAccountById(@PathVariable(value = "id") Integer id){
    return new ResponseEntity<>(appService.findAccountById(id), HttpStatus.OK);
  }
  
  @GetMapping("/accounts")
  public ResponseEntity<Flux<AccountDto>> findAllAccounts(@RequestParam(value = "ownerId", required = false) Integer ownerId){
    return new ResponseEntity<>(appService.findAccounts(ownerId), HttpStatus.OK);
  }
  
  @PostMapping("/accounts")
  public ResponseEntity<Mono<AccountDto>> createAccount(@RequestBody AccountDto accountDto){
    return new ResponseEntity<>(appService.createAccount(accountDto), HttpStatus.CREATED);
  }
  
  @GetMapping("/accounts/transactions")
  public ResponseEntity<Flux<AccountTransactionDto>> findAllTransactions(){
    return new ResponseEntity<>(appService.findAllTransactions(), HttpStatus.OK);
  }
  
  @GetMapping("/accounts/{id}/transactions")
  public ResponseEntity<Flux<AccountTransactionDto>> findTransactions(
    @PathVariable(value = "id", required = false) Integer accountId){
    return new ResponseEntity<>(appService.findTransactions(accountId), HttpStatus.OK);
  }
  
  @PostMapping("/accounts/{id}/transactions")
  public ResponseEntity<Mono<AccountTransactionDto>> createTransaction(
    @PathVariable(value = "id") Integer accountId,
    @RequestBody AccountTransactionDto accountTransactionDto){
    return new ResponseEntity<>(appService.createTransaction(accountId,accountTransactionDto),
      HttpStatus.CREATED);
  }
  
}
