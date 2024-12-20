package com.jpdr.apps.demo.webflux.account.service.dto.account;

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
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountDto implements Serializable {
  
  @JsonInclude(Include.NON_NULL)
  Integer id;
  @JsonInclude(Include.NON_NULL)
  UUID number;
  @NonNull
  Integer ownerId;
  String ownerName;
  @JsonInclude(Include.NON_NULL)
  BigDecimal balance;
  @JsonInclude(Include.NON_NULL)
  Integer lastTransactionId;
  @JsonInclude(Include.NON_EMPTY)
  String lastTransactionDate;
  @JsonInclude(Include.NON_NULL)
  Boolean isActive;
  @JsonInclude(Include.NON_EMPTY)
  String creationDate;
  @JsonInclude(Include.NON_EMPTY)
  String deletionDate;
  
}
