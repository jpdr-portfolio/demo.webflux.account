package com.jpdr.apps.demo.webflux.account.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class DtoSerializer<T> implements RedisSerializer<T> {
  
  private final ObjectMapper objectMapper;
  private final Class<T> type;
  
  @Override
  public byte[] serialize(T value) throws SerializationException {
    try{
      String serializedObject = this.objectMapper.writeValueAsString(value);
      return serializedObject.getBytes(StandardCharsets.UTF_8);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
  
  @Override
  public T deserialize(byte[] bytes) throws SerializationException {
    try {
      String serializedObject = new String(bytes);
      return this.objectMapper.readValue(serializedObject, this.type);
    } catch (JsonMappingException e) {
      throw new RuntimeException(e);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
