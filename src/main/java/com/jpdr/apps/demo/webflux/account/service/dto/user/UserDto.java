package com.jpdr.apps.demo.webflux.account.service.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto implements Serializable {
  
  @JsonInclude(Include.NON_EMPTY)
  Integer id;
  @NonNull
  String name;
  @NonNull
  String email;
  @JsonInclude(Include.NON_EMPTY)
  Boolean isActive;
  @JsonInclude(Include.NON_EMPTY)
  String creationDate;
  @JsonInclude(Include.NON_EMPTY)
  String deletionDate;
  
}
