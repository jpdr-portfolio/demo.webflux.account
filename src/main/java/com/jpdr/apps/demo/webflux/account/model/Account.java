package com.jpdr.apps.demo.webflux.account.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Table("account")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account {
  
  @Id
  @Column("id")
  Integer id;
  @Column("number")
  UUID number;
  @Column("owner_id")
  Integer ownerId;
  @Column("owner_name")
  String ownerName;
  @Column("balance")
  BigDecimal balance;
  @Column("last_transaction_id")
  Integer lastTransactionId;
  @Column("last_transaction_date")
  OffsetDateTime lastTransactionDate;
  @Column("is_active")
  Boolean isActive;
  @Column("creation_date")
  OffsetDateTime creationDate;
  @Column("deletion_date")
  OffsetDateTime deletionDate;
  
}
