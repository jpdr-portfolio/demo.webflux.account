package com.jpdr.apps.demo.webflux.account.repository.user;

import com.jpdr.apps.demo.webflux.account.service.dto.user.UserDto;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository {

  Mono<UserDto> getUserById(Integer userId);

}
