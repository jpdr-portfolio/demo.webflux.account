package com.jpdr.apps.demo.webflux.account.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpdr.apps.demo.webflux.account.exception.user.UserNotFoundException;
import com.jpdr.apps.demo.webflux.account.exception.user.UserRepositoryException;
import com.jpdr.apps.demo.webflux.account.exception.dto.ErrorDto;
import com.jpdr.apps.demo.webflux.account.repository.user.impl.UserRepositoryImpl;
import com.jpdr.apps.demo.webflux.account.service.dto.user.UserDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

import static com.jpdr.apps.demo.webflux.account.util.TestDataGenerator.getUserDto;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
class UserRepositoryTest {
  
  private UserRepositoryImpl userRepository;
  
  private static MockWebServer mockWebServer;
  
  private ObjectMapper objectMapper;
  
  @BeforeAll
  static void setupOnce() throws IOException{
    mockWebServer = new MockWebServer();
    mockWebServer.start(9999);
  }
  
  @BeforeEach
  void setupEach() {
    String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
    WebClient webClient = WebClient.builder()
      .baseUrl(baseUrl).build();
    userRepository = new UserRepositoryImpl(webClient);
    objectMapper = new ObjectMapper();
  }
  
  @Test
  @DisplayName("OK - Find By User By Id")
  void givenUserIdWhenFindUserByIdThenThenReturnUser() throws JsonProcessingException {
    UserDto expectedUser = getUserDto();
    String responseBody = objectMapper.writeValueAsString(expectedUser);
    
    MockResponse response = new MockResponse();
    response.setResponseCode(HttpStatus.OK.value());
    response.setBody(responseBody);
    response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    mockWebServer.enqueue(response);
    
    StepVerifier.create(userRepository.getUserById(1))
      .assertNext(dto -> {
        assertEquals(expectedUser.getId(), dto.getId());
        assertEquals(expectedUser.getName(), dto.getName());
        assertEquals(expectedUser.getEmail(), dto.getEmail());
        assertEquals(expectedUser.getIsActive(), dto.getIsActive());
        assertEquals(expectedUser.getCreationDate(), dto.getCreationDate());
        assertEquals(expectedUser.getDeletionDate(), dto.getDeletionDate());
      })
      .expectComplete()
      .verify();
  }
  
  
  @Test
  @DisplayName("Error - Find By User By Id - Not Found")
  void givenNotFoundUserWhenFindUserByIdThenReturnError() throws JsonProcessingException {
    ErrorDto expectedError = new ErrorDto("User 1 not found");
    String responseBody = objectMapper.writeValueAsString(expectedError);
    
    MockResponse response = new MockResponse();
    response.setResponseCode(HttpStatus.NOT_FOUND.value());
    response.setBody(responseBody);
    response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    mockWebServer.enqueue(response);
    
    StepVerifier.create(userRepository.getUserById(1))
      .expectError(UserNotFoundException.class)
      .verify();
  }
  
  @Test
  @DisplayName("Error - Find By User By Id - Internal Server Error")
  void givenInternalServerErrorWhenFindUserByIdThenReturnError() {
    
    MockResponse response = new MockResponse();
    response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    mockWebServer.enqueue(response);
    
    StepVerifier.create(userRepository.getUserById(1))
      .expectError(UserRepositoryException.class)
      .verify();
  }
  
  @AfterAll
  static void closeOnce() throws IOException {
    mockWebServer.shutdown();
  }
  
  
  
}
