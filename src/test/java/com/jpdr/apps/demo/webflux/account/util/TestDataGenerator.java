package com.jpdr.apps.demo.webflux.account.util;

import com.jpdr.apps.demo.webflux.account.model.Account;
import com.jpdr.apps.demo.webflux.account.model.AccountTransaction;
import com.jpdr.apps.demo.webflux.account.service.dto.account.AccountDto;
import com.jpdr.apps.demo.webflux.account.service.dto.account.AccountTransactionDto;
import com.jpdr.apps.demo.webflux.account.service.dto.user.UserDto;
import com.jpdr.apps.demo.webflux.account.service.enums.AccountTransactionTypeEnum;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class TestDataGenerator {
  
  public static final String NAME = "John Smith";
  public static final String EMAIL = "johnsmith@mail.com";
  public static final String CREATION_DATE = "2024-10-14T10:39:45.732446-03:00";
  public static final UUID NUMBER = UUID.fromString("f9bcbc7c-8bbe-4306-8feb-7fd598686d4e");
  public static final String DESCRIPTION = "Transaction Description";
  
  
  public static List<AccountDto> getAccountDtos(){
    return Stream.iterate(1, n -> n+1)
      .limit(3)
      .map(TestDataGenerator::getAccountDto)
      .toList();
  }
  
  public static AccountDto getAccountDto(){
    return getAccountDto(1);
  }
  
  public static AccountDto getAccountDto(int accountId){
    return AccountDto.builder()
      .id(accountId)
      .ownerId(1)
      .ownerName(NAME)
      .creationDate(CREATION_DATE)
      .deletionDate(null)
      .balance(BigDecimal.ZERO)
      .lastTransactionDate(null)
      .lastTransactionId(null)
      .number(NUMBER)
      .isActive(true)
      .build();
  }
  
  public static AccountDto getNewAccountDto(){
    return AccountDto.builder()
      .id(null)
      .ownerId(1)
      .ownerName(null)
      .creationDate(null)
      .deletionDate(null)
      .balance(null)
      .lastTransactionDate(null)
      .lastTransactionId(null)
      .number(null)
      .isActive(null)
      .build();
  }
  
  public static List<Account> getAccounts(){
    return Stream.iterate(1, n -> n + 1)
      .limit(3)
      .map(TestDataGenerator::getAccount)
      .toList();
  }
  
  public static Account getAccount(){
    return getAccount(1);
  }
  
  public static Account getAccount(int accountId){
    return Account.builder()
      .id(accountId)
      .ownerId(1)
      .ownerName(NAME)
      .creationDate(OffsetDateTime.parse(CREATION_DATE))
      .deletionDate(null)
      .balance(BigDecimal.ZERO)
      .lastTransactionDate(null)
      .lastTransactionId(null)
      .number(NUMBER)
      .isActive(true)
      .build();
  }
  
  public static UserDto getUserDto(){
    return UserDto.builder()
      .id(1)
      .email(EMAIL)
      .name(NAME)
      .creationDate(CREATION_DATE)
      .deletionDate(null)
      .isActive(true)
      .build();
  }
  
  public static List<AccountTransaction> getAccountTransactions(){
    return Stream.iterate(1, n -> n + 1)
      .limit(3)
      .map(i ->
        getAccountTransaction(i, AccountTransactionTypeEnum.CREDIT,BigDecimal.valueOf( i * 100.00),
          BigDecimal.valueOf((i - 1) * 100.00 )))
      .toList();
  }
  
  
  public static AccountTransaction getAccountTransaction(AccountTransactionTypeEnum type,
    BigDecimal amount){
    return getAccountTransaction(1, type, amount, BigDecimal.ZERO);
  }
  
  public static AccountTransaction getAccountTransaction(int transactionId,
    AccountTransactionTypeEnum type,
    BigDecimal amount, BigDecimal previousBalance){
    return AccountTransaction.builder()
      .id(transactionId)
      .accountId(1)
      .transactionDate(OffsetDateTime.parse(CREATION_DATE))
      .transactionAmount(amount)
      .transactionDescription(DESCRIPTION)
      .transactionType(type.getValue())
      .previousBalance(previousBalance)
      .currentBalance(previousBalance.add(amount))
      .build();
  }
  
  public static AccountTransactionDto getNewAccountTransactionDto(AccountTransactionTypeEnum type,
    BigDecimal amount){
    return AccountTransactionDto.builder()
      .id(null)
      .transactionDate(null)
      .transactionAmount(amount)
      .transactionDescription(DESCRIPTION)
      .transactionType(type)
      .previousBalance(null)
      .currentBalance(null)
      .build();
  }
  
  public static List<AccountTransactionDto> getAccountTransactionDtos(AccountTransactionTypeEnum type){
    return Stream.iterate(1, n-> n+1)
      .limit(3)
      .map(i -> getAccountTransactionDto(i, type, BigDecimal.valueOf(i*100.00),
        BigDecimal.valueOf((i-1)*100.00)))
      .toList();
  }
  
  public static AccountTransactionDto getAccountTransactionDto(int transactionId,
    AccountTransactionTypeEnum type, BigDecimal amount, BigDecimal previousBalance){
    return AccountTransactionDto.builder()
      .id(transactionId)
      .transactionDate((CREATION_DATE))
      .transactionAmount(amount)
      .transactionDescription(DESCRIPTION)
      .transactionType(type)
      .previousBalance(previousBalance)
      .currentBalance(previousBalance.add(amount))
      .build();
  }

}
