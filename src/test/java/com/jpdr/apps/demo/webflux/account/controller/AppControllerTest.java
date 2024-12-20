package com.jpdr.apps.demo.webflux.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpdr.apps.demo.webflux.account.service.AppService;
import com.jpdr.apps.demo.webflux.account.service.dto.account.AccountDto;
import com.jpdr.apps.demo.webflux.account.service.dto.account.AccountTransactionDto;
import com.jpdr.apps.demo.webflux.account.service.enums.AccountTransactionTypeEnum;
import com.jpdr.apps.demo.webflux.eventlogger.component.EventLogger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static com.jpdr.apps.demo.webflux.account.util.TestDataGenerator.getAccountDto;
import static com.jpdr.apps.demo.webflux.account.util.TestDataGenerator.getAccountDtos;
import static com.jpdr.apps.demo.webflux.account.util.TestDataGenerator.getAccountTransactionDto;
import static com.jpdr.apps.demo.webflux.account.util.TestDataGenerator.getAccountTransactionDtos;
import static com.jpdr.apps.demo.webflux.account.util.TestDataGenerator.getNewAccountDto;
import static com.jpdr.apps.demo.webflux.account.util.TestDataGenerator.getNewAccountTransactionDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class AppControllerTest {
  
  @Autowired
  private WebTestClient webTestClient;
  @MockBean
  private AppService appService;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private EventLogger eventLogger;
  
  @Test
  @DisplayName("OK - Find Account By Id")
  void givenAccountWhenFindByIdThenReturnAccount() {
    
    AccountDto expectedAccount = getAccountDto();
    
    when(appService.findAccountById(anyInt())).thenReturn(Mono.just(expectedAccount));
    
    FluxExchangeResult<AccountDto> exchangeResult = this.webTestClient.get()
      .uri("/accounts" + "/" + 1)
      .exchange()
      .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
      .expectStatus()
        .isOk()
      .returnResult(AccountDto.class);
    
    StepVerifier.create(exchangeResult.getResponseBody())
      .assertNext(receivedAccount -> assertEquals(expectedAccount, receivedAccount))
      .expectComplete()
      .verify();
  
  }
  
  @Test
  @DisplayName("OK - Find All Accounts")
  void givenNullOwnerWhenFindAllAccountsThenReturnAccounts() throws Exception{
    
    List<AccountDto> expectedAccounts = getAccountDtos();
    
    when(appService.findAccounts(isNull()))
      .thenReturn(Mono.just(expectedAccounts));
    
    String expectedBody = objectMapper.writeValueAsString(expectedAccounts);
    
    FluxExchangeResult<String> exchangeResult = this.webTestClient.get()
      .uri("/accounts")
      .exchange()
      .expectHeader()
      .contentType(MediaType.APPLICATION_JSON)
      .expectStatus()
      .isOk()
      .returnResult(String.class);
    
    StepVerifier.create(exchangeResult.getResponseBody())
      .assertNext(receivedBody -> assertEquals(expectedBody, receivedBody))
      .expectComplete()
      .verify();
    
  }
  
  
  
  @Test
  @DisplayName("OK - Find All Accounts By Owner Id")
  void givenOwnerIdWhenFindAllByOwnerIdThenReturnAccounts() throws Exception{
    
    List<AccountDto> expectedAccounts = getAccountDtos();
    
    when(appService.findAccounts(anyInt()))
      .thenReturn(Mono.just(expectedAccounts));
    
    String expectedBody = objectMapper.writeValueAsString(expectedAccounts);
    
    FluxExchangeResult<String> exchangeResult = this.webTestClient.get()
      .uri("/accounts?ownerId=1")
      .exchange()
      .expectHeader()
      .contentType(MediaType.APPLICATION_JSON)
      .expectStatus()
      .isOk()
      .returnResult(String.class);
    
    StepVerifier.create(exchangeResult.getResponseBody())
      .assertNext(receivedBody -> assertEquals(expectedBody, receivedBody))
      .expectComplete()
      .verify();
    
  }
  
  @Test
  @DisplayName("OK - Create Account")
  void givenAccountWhenCreateAccountThenReturnAccount(){
    
    AccountDto requestAccount = getNewAccountDto();
    AccountDto expectedAccount = getAccountDto();
    
    when(appService.createAccount(any(AccountDto.class)))
      .thenReturn(Mono.just(expectedAccount));
    
    FluxExchangeResult<AccountDto> exchangeResult = this.webTestClient.post()
      .uri("/accounts?ownerId=1")
      .bodyValue(requestAccount)
      .exchange()
      .expectHeader()
      .contentType(MediaType.APPLICATION_JSON)
      .expectStatus()
      .isCreated()
      .returnResult(AccountDto.class);
    
    StepVerifier.create(exchangeResult.getResponseBody())
      .assertNext(receivedAccount -> assertEquals(expectedAccount, receivedAccount))
      .expectComplete()
      .verify();
    
  }

  @Test
  @DisplayName("OK - Create Transaction - Deposit Funds")
  void givenAccountWhenDepositFundsThenReturnAccount(){

    AccountTransactionDto requestTransaction = getNewAccountTransactionDto(
      AccountTransactionTypeEnum.CREDIT, BigDecimal.valueOf(100.00));
    AccountDto expectedAccount = getAccountDto();
    expectedAccount.setBalance(BigDecimal.valueOf(100.00));
    AccountTransactionDto expectedTransaction = getAccountTransactionDto(1,
      AccountTransactionTypeEnum.CREDIT, BigDecimal.valueOf(100.00),BigDecimal.valueOf(200.00));

    when(appService.createTransaction(anyInt(),any(AccountTransactionDto.class)))
      .thenReturn(Mono.just(expectedTransaction));

    FluxExchangeResult<AccountTransactionDto> exchangeResult = this.webTestClient.post()
      .uri("/accounts" +"/"+ "1" +"/transactions")
      .accept(MediaType.APPLICATION_JSON)
      .bodyValue(requestTransaction)
      .exchange()
      .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
      .expectStatus()
        .isCreated()
      .returnResult(AccountTransactionDto.class);

    StepVerifier.create(exchangeResult.getResponseBody())
      .assertNext(receivedBody -> assertEquals(expectedTransaction, receivedBody))
      .expectComplete()
      .verify();

  }

  @Test
  @DisplayName("OK - Create Transaction - Withdraw Funds")
  void givenAccountWhenWithdrawFundsThenReturnAccount(){

    AccountTransactionDto requestTransaction = getNewAccountTransactionDto(
      AccountTransactionTypeEnum.DEBIT, BigDecimal.valueOf(100.00));
    AccountDto expectedAccount = getAccountDto();
    expectedAccount.setBalance(BigDecimal.valueOf(100.00));
    AccountTransactionDto expectedTransaction = getAccountTransactionDto(1,
      AccountTransactionTypeEnum.CREDIT, BigDecimal.valueOf(100.00),BigDecimal.ZERO);

    when(appService.createTransaction(anyInt(),any(AccountTransactionDto.class)))
      .thenReturn(Mono.just(expectedTransaction));

    FluxExchangeResult<AccountTransactionDto> exchangeResult = this.webTestClient.post()
      .uri("/accounts" +"/"+ "1" +"/transactions")
      .bodyValue(requestTransaction)
      .exchange()
      .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
      .expectStatus()
        .isCreated()
      .returnResult(AccountTransactionDto.class);

    StepVerifier.create(exchangeResult.getResponseBody())
      .assertNext(receivedAccount -> assertEquals(expectedTransaction, receivedAccount))
      .expectComplete()
      .verify();

  }
  
  @Test
  @DisplayName("OK - Find All Transactions")
  void givenTransactionsWhenFindAllTransactionsThenReturnTransactions() throws Exception{
    
    List<AccountTransactionDto> expectedTransactions =
      getAccountTransactionDtos(AccountTransactionTypeEnum.CREDIT);
    
    when(appService.findAllTransactions())
      .thenReturn(Mono.just(expectedTransactions));
    
    String expectedBody = objectMapper.writeValueAsString(expectedTransactions);
    
    FluxExchangeResult<String> exchangeResult = this.webTestClient.get()
      .uri("/accounts" +"/" +"transactions")
      .exchange()
      .expectHeader()
      .contentType(MediaType.APPLICATION_JSON)
      .expectStatus()
      .isOk()
      .returnResult(String.class);
    
    StepVerifier.create(exchangeResult.getResponseBody())
      .assertNext(receivedBody -> assertEquals(expectedBody, receivedBody))
      .expectComplete()
      .verify();
    
  }

  @Test
  @DisplayName("OK - Find Transactions")
  void givenAccountIdWhenFindTransactionsThenReturnTransactions() throws Exception{

    List<AccountTransactionDto> expectedTransactions =
      getAccountTransactionDtos(AccountTransactionTypeEnum.CREDIT);

    when(appService.findTransactions(anyInt()))
      .thenReturn(Mono.just(expectedTransactions));

    String expectedBody = objectMapper.writeValueAsString(expectedTransactions);

    FluxExchangeResult<String> exchangeResult = this.webTestClient.get()
      .uri("/accounts" +"/"+ "1" +"/transactions")
      .exchange()
      .expectHeader()
      .contentType(MediaType.APPLICATION_JSON)
      .expectStatus()
      .isOk()
      .returnResult(String.class);

    StepVerifier.create(exchangeResult.getResponseBody())
      .assertNext(receivedBody -> assertEquals(expectedBody, receivedBody))
      .expectComplete()
      .verify();

  }
  
}
