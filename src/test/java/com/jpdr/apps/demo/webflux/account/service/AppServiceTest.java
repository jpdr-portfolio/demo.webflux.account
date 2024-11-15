package com.jpdr.apps.demo.webflux.account.service;

import com.jpdr.apps.demo.webflux.account.exception.account.AccountNotFoundException;
import com.jpdr.apps.demo.webflux.account.exception.account.InsufficientFundsException;
import com.jpdr.apps.demo.webflux.account.exception.user.UserNotFoundException;
import com.jpdr.apps.demo.webflux.account.model.Account;
import com.jpdr.apps.demo.webflux.account.model.AccountTransaction;
import com.jpdr.apps.demo.webflux.account.repository.account.AccountRepository;
import com.jpdr.apps.demo.webflux.account.repository.account.AccountTransactionRepository;
import com.jpdr.apps.demo.webflux.account.repository.user.UserRepository;
import com.jpdr.apps.demo.webflux.account.service.dto.account.AccountDto;
import com.jpdr.apps.demo.webflux.account.service.dto.account.AccountTransactionDto;
import com.jpdr.apps.demo.webflux.account.service.dto.user.UserDto;
import com.jpdr.apps.demo.webflux.account.service.enums.AccountTransactionTypeEnum;
import com.jpdr.apps.demo.webflux.account.service.impl.AppServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.jpdr.apps.demo.webflux.account.util.TestDataGenerator.getAccount;
import static com.jpdr.apps.demo.webflux.account.util.TestDataGenerator.getAccountDto;
import static com.jpdr.apps.demo.webflux.account.util.TestDataGenerator.getAccountTransaction;
import static com.jpdr.apps.demo.webflux.account.util.TestDataGenerator.getAccountTransactions;
import static com.jpdr.apps.demo.webflux.account.util.TestDataGenerator.getAccounts;
import static com.jpdr.apps.demo.webflux.account.util.TestDataGenerator.getNewAccountTransactionDto;
import static com.jpdr.apps.demo.webflux.account.util.TestDataGenerator.getUserDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
class AppServiceTest {
  
  @InjectMocks
  private AppServiceImpl appService;
  
  @Mock
  private AccountRepository accountRepository;
  
  @Mock
  private AccountTransactionRepository accountTransactionRepository;
  
  @Mock
  private UserRepository userRepository;

  
  @Test
  @DisplayName("OK - Find By Id")
  void givenIdWhenFindByIdThenReturnAccount(){
    
    Account account = getAccount();
    
    when(accountRepository.findByIdAndIsActiveIsTrue(anyInt()))
      .thenReturn(Mono.just(account));
    
    StepVerifier.create(appService.findAccountById(1))
      .assertNext(dto -> assertAccount(account,dto))
      .expectComplete()
      .verify();
  }
  
  @Test
  @DisplayName("Error - Find By Id - Not found")
  void givenIdWhenFindByIdThenReturnError(){
    
    when(accountRepository.findByIdAndIsActiveIsTrue(anyInt()))
      .thenReturn(Mono.empty());
    
    StepVerifier.create(appService.findAccountById(1))
      .expectError(AccountNotFoundException.class)
      .verify();
  
  }
  
  
  @Test
  @DisplayName("OK - Find Accounts - No OwnerId")
  void givenNoOwnerIdWhenFindAccountsThenReturnAccounts(){
    
    List<Account> accounts = getAccounts();
    Map<Integer, Account> accountsMap = accounts.stream()
      .collect(Collectors.toMap(Account::getId, Function.identity()));
    
    when(accountRepository.findAllByIsActiveIsTrue())
      .thenReturn(Flux.fromIterable(accounts));
    
    StepVerifier.create(appService.findAccounts(null))
      .assertNext(dtos -> {
        for(AccountDto dto : dtos){
          assertAccount(accountsMap.get(dto.getId()),dto);
        }
      })
      .expectComplete()
      .verify();
  }
  
  
  @Test
  @DisplayName("OK - Find Accounts - By OwnerId")
  void givenOwnerIdWhenFindByOwnerIdThenReturnAccounts(){
    
    List<Account> accounts = getAccounts();
    Map<Integer, Account> accountsMap = accounts.stream()
      .collect(Collectors.toMap(Account::getId, Function.identity()));
    UserDto userDto = getUserDto();
    
    when(userRepository.getUserById(anyInt()))
      .thenReturn(Mono.just(userDto));
    when(accountRepository.findByOwnerIdAndIsActiveIsTrue(anyInt()))
      .thenReturn(Flux.fromIterable(accounts));
    
    StepVerifier.create(appService.findAccounts(1))
      .assertNext(dtos -> {
        for(AccountDto dto : dtos){
          assertAccount(accountsMap.get(dto.getId()),dto);
        }
      })
      .expectComplete()
      .verify();
  }
  
  @Test
  @DisplayName("OK - Find All By OwnerId - No Accounts")
  void givenOwnerIdWhenFindByOwnerIdThenReturnEmpty(){
    
    UserDto userDto = getUserDto();
    
    when(userRepository.getUserById(anyInt()))
      .thenReturn(Mono.just(userDto));
    
    when(accountRepository.findByOwnerIdAndIsActiveIsTrue(anyInt()))
      .thenReturn(Flux.empty());
    
    StepVerifier.create(appService.findAllAccountsByOwnerId(1))
      .assertNext(accounts -> accounts.isEmpty())
      .expectComplete()
      .verify();
    
  }
  
  
  
  
  

  @Test
  @DisplayName("OK - Create Transaction - Deposit Funds")
  void givenAccountWhenDepositFundsThenReturnAccount(){

    Account account = getAccount();
    AccountTransaction accountTransaction = getAccountTransaction(AccountTransactionTypeEnum.CREDIT,
      BigDecimal.valueOf(100.00));
    Account savedAccount = getAccount();
    savedAccount.setBalance(BigDecimal.valueOf(100.00));
    AccountTransactionDto depositTransaction = getNewAccountTransactionDto(AccountTransactionTypeEnum.CREDIT,
      BigDecimal.valueOf(100.00));

    when(accountRepository.findByIdAndIsActiveIsTrue(anyInt()))
      .thenReturn(Mono.just(account));
    when(accountTransactionRepository.save(any(AccountTransaction.class)))
      .thenReturn(Mono.just(accountTransaction));
    when(accountRepository.save(any(Account.class)))
      .thenReturn(Mono.just(savedAccount));

    StepVerifier.create(appService.createTransaction(1, depositTransaction))
      .assertNext(dto ->  assertEquals(savedAccount.getBalance(),dto.getCurrentBalance()))
      .expectComplete()
      .verify();

  }


  @Test
  @DisplayName("Error - Create Transaction - Deposit Funds - Account not found")
  void givenAccountNotFoundWhenDepositFundsThenReturnError(){

    AccountTransactionDto depositTransaction = getNewAccountTransactionDto(AccountTransactionTypeEnum.CREDIT,
      BigDecimal.valueOf(100.00));

    when(accountRepository.findByIdAndIsActiveIsTrue(anyInt()))
      .thenReturn(Mono.empty());

    StepVerifier.create(appService.createTransaction(1, depositTransaction))
      .expectError(AccountNotFoundException.class)
      .verify();

  }




  @Test
  @DisplayName("OK - Create Transaction - Withdraw Funds")
  void givenAccountWhenWithdrawFundsThenReturnAccount(){

    Account account = getAccount();
    account.setBalance(BigDecimal.valueOf(200.00));
    AccountTransaction accountTransaction = getAccountTransaction(AccountTransactionTypeEnum.DEBIT,
      BigDecimal.valueOf(100.00));
    Account savedAccount = getAccount();
    savedAccount.setBalance(BigDecimal.valueOf(100.00));
    AccountTransactionDto withdrawTransaction = getNewAccountTransactionDto(AccountTransactionTypeEnum.DEBIT,
      BigDecimal.valueOf(100.00));

    when(accountRepository.findByIdAndIsActiveIsTrue(anyInt()))
      .thenReturn(Mono.just(account));
    when(accountTransactionRepository.save(any(AccountTransaction.class)))
      .thenReturn(Mono.just(accountTransaction));
    when(accountRepository.save(any(Account.class)))
      .thenReturn(Mono.just(savedAccount));

    StepVerifier.create(appService.createTransaction(1, withdrawTransaction))
      .assertNext(dto ->  assertEquals(savedAccount.getBalance(),dto.getCurrentBalance()))
      .expectComplete()
      .verify();

  }


  @Test
  @DisplayName("Error - Create Transaction - Withdraw Funds - Not enough funds")
  void givenNotEnoughFundsWhenWithdrawFundsThenReturnError(){

    Account account = getAccount();
    account.setBalance(BigDecimal.valueOf(100.00));
    AccountTransactionDto withdrawTransaction = getNewAccountTransactionDto(AccountTransactionTypeEnum.DEBIT,
      BigDecimal.valueOf(200.00));

    when(accountRepository.findByIdAndIsActiveIsTrue(anyInt()))
      .thenReturn(Mono.just(account));

    StepVerifier.create(appService.createTransaction(1, withdrawTransaction))
      .expectError(InsufficientFundsException.class)
      .verify();

  }


  @Test
  @DisplayName("Error - Create Transaction - Withdraw Funds - Account not found")
  void givenAccountNotFoundWhenWithdrawFundsThenReturnError(){

    AccountTransactionDto depositTransaction = getNewAccountTransactionDto(AccountTransactionTypeEnum.DEBIT,
      BigDecimal.valueOf(100.00));

    when(accountRepository.findByIdAndIsActiveIsTrue(anyInt()))
      .thenReturn(Mono.empty());

    StepVerifier.create(appService.createTransaction(1, depositTransaction))
      .expectError(AccountNotFoundException.class)
      .verify();

  }

  
  
  
  
  
  
  
  @Test
  @DisplayName("OK - Create Account")
  void givenUserIdWhenCreateAccountThenReturnAccount(){
    
    AccountDto accountDto = getAccountDto();
    Account account = getAccount();
    UserDto userDto = getUserDto();
  
    when(userRepository.getUserById(anyInt())).thenReturn(Mono.just(userDto));
    when(accountRepository.save(any(Account.class)))
      .thenAnswer(i -> {
        Account savedAccount = i.getArgument(0);
        savedAccount.setId(1);
        return Mono.just(savedAccount);
      });
    
    StepVerifier.create(appService.createAccount(accountDto))
      .assertNext(dto -> assertAccount(account, dto))
      .expectComplete()
      .verify();
  
  }
  
  @Test
  @DisplayName("Error - Create Account - User doesn't exist")
  void givenUserIdWhenCreateAccountThenReturnError(){
    
    AccountDto accountDto = getAccountDto();
    
    when(userRepository.getUserById(anyInt())).thenReturn(Mono.error(new UserNotFoundException(2, new RuntimeException())));
    
    StepVerifier.create(appService.createAccount(accountDto))
      .expectError(UserNotFoundException.class)
      .verify();
    
  }
  
  @Test
  @DisplayName("OK - Find All Transactions")
  void givenTransactionsWhenFindAllTransactionsThenReturnTransactions(){
    
    List<AccountTransaction> accountTransactions = getAccountTransactions();
    Map<Integer, AccountTransaction> accountTransactionsMap = accountTransactions.stream()
      .collect(Collectors.toMap(AccountTransaction::getId, Function.identity()));
    
    when(accountTransactionRepository.findAll())
      .thenReturn(Flux.fromIterable(accountTransactions));
    
    StepVerifier.create(appService.findAllTransactions())
      .assertNext(dtos -> {
        for(AccountTransactionDto dto : dtos){
          assertEquals(accountTransactionsMap.get(dto.getId()).getTransactionAmount(),
            dto.getTransactionAmount());
        }
      })
      .expectComplete()
      .verify();
    
  }
  
  


  @Test
  @DisplayName("OK - Find Transactions")
  void givenAccountIdWhenFindTransactionsThenReturnTransactions(){

    Account account = getAccount();
    List<AccountTransaction> accountTransactions = getAccountTransactions();
    Map<Integer, AccountTransaction> accountTransactionsMap = accountTransactions.stream()
      .collect(Collectors.toMap(AccountTransaction::getId, Function.identity()));

    when(accountRepository.findByIdAndIsActiveIsTrue(anyInt()))
      .thenReturn(Mono.just(account));
    when(accountTransactionRepository.findByAccountIdOrderByTransactionDateDesc(anyInt()))
      .thenReturn(Flux.fromIterable(accountTransactions));

    StepVerifier.create(appService.findTransactionsByAccountId(1))
      .assertNext(dtos -> {
        for(AccountTransactionDto dto : dtos){
          assertEquals(accountTransactionsMap.get(dto.getId()).getTransactionAmount(),
            dto.getTransactionAmount());
        }
      })
      .expectComplete()
      .verify();

  }

  @Test
  @DisplayName("Error - Find Transactions - Account Not Found")
  void givenAccountNotFoundWhenFindTransactionsThenReturnError(){

    when(accountRepository.findByIdAndIsActiveIsTrue(anyInt()))
      .thenReturn(Mono.empty());

    StepVerifier.create(appService.findTransactions(1))
      .expectError(AccountNotFoundException.class)
      .verify();

  }

  @Test
  @DisplayName("OK - Find Transactions - No transactions")
  void givenNoTransactionsWhenFindTransactionsThenReturnEmpty(){

    Account account = getAccount();

    when(accountRepository.findByIdAndIsActiveIsTrue(anyInt()))
      .thenReturn(Mono.just(account));
    when(accountTransactionRepository.findByAccountIdOrderByTransactionDateDesc(anyInt()))
      .thenReturn(Flux.empty());

    StepVerifier.create(appService.findTransactions(1))
      .assertNext(transactions -> transactions.isEmpty())
      .expectComplete()
      .verify();

  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  private void assertAccount(Account entity, AccountDto dto){
    assertEquals(entity.getId(), dto.getId());
    assertNotNull(dto.getNumber());
    assertEquals(entity.getBalance(), dto.getBalance());
    assertEquals(entity.getIsActive(), dto.getIsActive());
    assertTrue(StringUtils.isBlank(dto.getDeletionDate()));
    assertNotNull(dto.getCreationDate());
    assertTrue(StringUtils.isBlank(dto.getLastTransactionDate()));
    assertEquals(entity.getLastTransactionId(), dto.getLastTransactionId());
    assertEquals(entity.getOwnerId(), dto.getOwnerId());
    assertEquals(entity.getOwnerName(), dto.getOwnerName());
  }
  
}
