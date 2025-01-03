package com.jpdr.apps.demo.webflux.account.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpdr.apps.demo.webflux.account.service.dto.account.AccountDto;
import com.jpdr.apps.demo.webflux.account.service.dto.user.UserDto;
import com.jpdr.apps.demo.webflux.commons.caching.DtoSerializer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@EnableCaching
@Configuration
public class CacheConfig {
  
  @Bean
  public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper){
    
    ObjectMapper mapper = objectMapper.copy()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
    mapper.findAndRegisterModules();
    
    DtoSerializer<AccountDto> accountDtoDtoSerializer = new DtoSerializer<>(mapper, AccountDto.class);
    DtoSerializer<UserDto> userDtoDtoSerializer = new DtoSerializer<>(mapper, UserDto.class);
    
    RedisSerializationContext.SerializationPair<AccountDto> accountDtoSerializationPair =
      RedisSerializationContext.SerializationPair.fromSerializer(accountDtoDtoSerializer);
    RedisSerializationContext.SerializationPair<UserDto> userDtoSerializationPair =
      RedisSerializationContext.SerializationPair.fromSerializer(userDtoDtoSerializer);
    
    RedisCacheConfiguration accountCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
      .serializeValuesWith(accountDtoSerializationPair);
    RedisCacheConfiguration userCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
      .serializeValuesWith(userDtoSerializationPair);
    
    return RedisCacheManager.builder(redisConnectionFactory)
      .withCacheConfiguration("accounts",accountCacheConfiguration)
      .withCacheConfiguration("users", userCacheConfiguration)
      .build();
  }

}
