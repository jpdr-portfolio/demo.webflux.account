package com.jpdr.apps.demo.webflux.account.service.impl;

import com.jpdr.apps.demo.webflux.account.exception.account.AccountNotFoundException;
import com.jpdr.apps.demo.webflux.account.exception.account.InsufficientFundsException;
import com.jpdr.apps.demo.webflux.account.model.Account;
import com.jpdr.apps.demo.webflux.account.model.AccountTransaction;
import com.jpdr.apps.demo.webflux.account.repository.account.AccountRepository;
import com.jpdr.apps.demo.webflux.account.repository.account.AccountTransactionRepository;
import com.jpdr.apps.demo.webflux.account.repository.user.UserRepository;
import com.jpdr.apps.demo.webflux.account.service.AppService;
import com.jpdr.apps.demo.webflux.account.service.dto.account.AccountDto;
import com.jpdr.apps.demo.webflux.account.service.dto.account.AccountTransactionDto;
import com.jpdr.apps.demo.webflux.account.service.dto.user.UserDto;
import com.jpdr.apps.demo.webflux.account.service.enums.AccountTransactionTypeEnum;
import com.jpdr.apps.demo.webflux.account.service.mapper.AccountMapper;
import com.jpdr.apps.demo.webflux.account.service.mapper.AccountTransactionMapper;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.jpdr.apps.demo.webflux.account.util.InputValidator.isValidAmount;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppServiceImpl implements AppService {
  
  private final AccountRepository accountRepository;
  private final AccountTransactionRepository accountTransactionRepository;
  private final UserRepository userRepository;
  
  
  @Override
  @Transactional
  public Mono<AccountDto> createAccount(AccountDto accountDto) {
    log.debug("createAccount");
    return this.userRepository.getUserById(accountDto.getOwnerId())
      .flatMap(userDto -> Mono.zip(
        Mono.just(this.getAccountForUser(userDto)),
        Mono.from(getRandomUUID())))
      .map(tuple -> {
        Account account = tuple.getT1();
        account.setNumber(tuple.getT2());
        return account;
      })
      .flatMap(this.accountRepository::save)
      .doOnNext(savedAccount -> log.debug(savedAccount.toString()))
      .map(AccountMapper.INSTANCE::entityToDto);
  }
  
  
  @Override
  public Mono<AccountDto> findAccountById(Integer accountId) {
    log.debug("findById");
    return this.accountRepository.findByIdAndIsActiveIsTrue(accountId)
      .switchIfEmpty(Mono.error(new AccountNotFoundException(accountId)))
      .doOnNext(account -> log.debug(account.toString()))
      .map(AccountMapper.INSTANCE::entityToDto);
  }
  
  @Override
  public Mono<List<AccountDto>> findAccounts(Integer ownerId) {
    log.debug("findAccounts");
    return Mono.just(Optional.ofNullable(ownerId))
      .flatMap(optional -> {
        if (optional.isPresent()) {
          return this.findAllAccountsByOwnerId(optional.get());
        }
        return this.findAllAccounts();
      });
  }
  
  @Override
  public Mono<List<AccountDto>> findAllAccounts() {
    log.debug("findAllAccounts");
    return this.accountRepository.findAllByIsActiveIsTrue()
      .map(AccountMapper.INSTANCE::entityToDto)
      .doOnNext(account -> log.debug(account.toString()))
      .collectList();
  }
  
  
  @Override
  public Mono<List<AccountDto>> findAllAccountsByOwnerId(Integer ownerId) {
    log.debug("findAllAccountsByOwnerId");
    return this.userRepository.getUserById(ownerId)
      .flatMapMany(userDto -> this.accountRepository.findByOwnerIdAndIsActiveIsTrue(
          userDto.getId()))
      .doOnNext(account -> log.debug(account.toString()))
      .map(AccountMapper.INSTANCE::entityToDto)
      .collectList();
  }
  

  

  
  @Override
  @Transactional
  public Mono<AccountTransactionDto> createTransaction(Integer accountId, AccountTransactionDto accountTransactionDto) {
    log.debug("createTransaction");
    return Mono.zip(
        Mono.just(accountId),
        Mono.just(accountTransactionDto),
        Mono.from(validateTransaction(accountTransactionDto)))
      .flatMap(tuple ->
        Mono.zip(
          Mono.just(tuple.getT2()),
          Mono.from(this.accountRepository.findByIdAndIsActiveIsTrue(tuple.getT1()))
            .switchIfEmpty(Mono.error(new AccountNotFoundException(tuple.getT1()))))
        )
      .flatMap(this::validateFunds)
      .flatMap(tuple ->
        Mono.zip(
          Mono.just(tuple.getT2()),
          Mono.just(getTransactionForAccount(tuple.getT2(), tuple.getT1()))))
      .flatMap(tuple ->
          Mono.zip(
            Mono.just(tuple.getT1()),
            Mono.from(this.accountTransactionRepository.save(tuple.getT2()))))
      .map(tuple -> {
        tuple.getT1().setBalance(tuple.getT2().getCurrentBalance());
        tuple.getT1().setLastTransactionId(tuple.getT2().getId());
        tuple.getT1().setLastTransactionDate(tuple.getT2().getTransactionDate());
        return tuple;
      })
      .flatMap(tuple ->
        Mono.zip(
          Mono.from(this.accountRepository.save(tuple.getT1())),
          Mono.just(tuple.getT2())))
      .map(tuple -> AccountTransactionMapper.INSTANCE.entityToDto(tuple.getT2()))
      .doOnNext(resultAccountTransactionDto -> log.debug(resultAccountTransactionDto.toString()));
      
  }
  
  
  @Override
  public Mono<List<AccountTransactionDto>> findTransactions(Integer accountId) {
    return Mono.just(Optional.ofNullable(accountId))
      .flatMap(optional -> {
        if(optional.isPresent()){
          return this.findTransactionsByAccountId(optional.get());
        }
        return this.findAllTransactions();
      });
  }
  
  @Override
  public Mono<List<AccountTransactionDto>> findTransactionsByAccountId(Integer accountId) {
    log.debug("findTransactionsByAccountId");
    return this.accountRepository.findByIdAndIsActiveIsTrue(accountId)
      .switchIfEmpty(Mono.error(new AccountNotFoundException(accountId)))
      .doOnNext(account -> log.debug(account.toString()))
      .flatMapMany(account -> this.accountTransactionRepository
        .findByAccountIdOrderByTransactionDateDesc(account.getId()))
      .doOnNext(transaction -> log.debug(transaction.toString()))
      .map(AccountTransactionMapper.INSTANCE::entityToDto)
      .collectList();
  }
  
  
  @Override
  public Mono<List<AccountTransactionDto>> findAllTransactions() {
    return this.accountTransactionRepository.findAll()
      .map(AccountTransactionMapper.INSTANCE::entityToDto)
      .doOnNext(account -> log.debug(account.toString()))
      .collectList();
  }
  
  private Account getAccountForUser(UserDto userDto){
    return Account.builder()
      .id(null)
      .ownerId(userDto.getId())
      .ownerName(userDto.getName())
      .balance(new BigDecimal(0))
      .lastTransactionDate(null)
      .lastTransactionId(null)
      .isActive(true)
      .creationDate(OffsetDateTime.now())
      .deletionDate(null)
      .build();
  }
  
  private AccountTransaction getTransactionForAccount(Account account,
    AccountTransactionDto accountTransactionDto){
    return AccountTransaction.builder()
      .id(null)
      .accountId(account.getId())
      .transactionDate(OffsetDateTime.now())
      .transactionType(accountTransactionDto.getTransactionType().getValue())
      .transactionAmount(accountTransactionDto.getTransactionAmount())
      .transactionDescription(accountTransactionDto.getTransactionDescription())
      .previousBalance(account.getBalance())
      .currentBalance(getCurrentBalance(account.getBalance(),
        accountTransactionDto.getTransactionAmount(), accountTransactionDto.getTransactionType()))
      .build();
  }
  
  private BigDecimal getCurrentBalance(BigDecimal balance, BigDecimal amount,
    AccountTransactionTypeEnum type){
    if(type.equals(AccountTransactionTypeEnum.CREDIT)){
      return balance.add(amount);
    }else{
      return balance.subtract(amount);
    }
  }

  private Mono<Tuple2<AccountTransactionDto, Account>> validateFunds(Tuple2<AccountTransactionDto, Account> inputTuple){
    return Mono.just(inputTuple)
      .flatMap(tuple ->{
          if (tuple.getT1().getTransactionType().equals(AccountTransactionTypeEnum.DEBIT) &&
            tuple.getT2().getBalance().compareTo(tuple.getT1().getTransactionAmount()) < 0) {
            return Mono.error(new InsufficientFundsException(tuple.getT2().getBalance()));
          }
          return Mono.just(tuple);
      });
  }
  
  private Mono<AccountTransactionDto> validateTransaction(AccountTransactionDto accountTransactionDto){
    return Mono.just(accountTransactionDto)
      .filter(accountTransaction -> isValidAmount(accountTransaction.getTransactionAmount()))
      .switchIfEmpty(Mono.error(
        new ValidationException("The transaction amount must be greater than zero.")))
      .filter(accountTransaction -> isValidAmount(accountTransactionDto.getTransactionAmount()))
      .switchIfEmpty(Mono.error(
        new ValidationException("The transaction description can't be empty.")));
  }
  
  public static Mono<UUID> getRandomUUID(){
    return Mono.fromCallable(UUID::randomUUID)
      .subscribeOn(Schedulers.boundedElastic());
  }
  
  
  
}
