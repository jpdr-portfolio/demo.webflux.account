package com.jpdr.apps.demo.webflux.account.repository.user.impl;

import com.jpdr.apps.demo.webflux.account.exception.user.UserNotFoundException;
import com.jpdr.apps.demo.webflux.account.exception.user.UserRepositoryException;
import com.jpdr.apps.demo.webflux.account.repository.user.UserRepository;
import com.jpdr.apps.demo.webflux.account.service.dto.user.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {
  
  private final WebClient webClient;
  
  public UserRepositoryImpl(@Qualifier(value = "userWebClient") WebClient webClient ){
    this.webClient = webClient;
  }
  
  @Override
  @Cacheable(key = "#userId", value = "users", sync = true)
  public Mono<UserDto> getUserById(Integer userId) {
    return this.webClient.get()
      .uri("/users/{userId}", userId)
      .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
      .retrieve()
      .onStatus(HttpStatus.NOT_FOUND::equals,
        response -> response.createException()
          .map(error -> new UserNotFoundException(userId,error)))
      .onStatus(HttpStatusCode::isError,
        response -> response.createException()
          .map(error -> new UserRepositoryException(userId, error))
        )
      .bodyToMono(UserDto.class);
  }
  
}
