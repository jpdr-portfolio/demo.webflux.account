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

@Data
@Table("account_transaction")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountTransaction {
  
  @Id
  @Column("id")
  Integer id;
  @Column("account_id")
  Integer accountId;
  @Column("transaction_date")
  OffsetDateTime transactionDate;
  @Column("transaction_type")
  String transactionType;
  @Column("transaction_amount")
  BigDecimal transactionAmount;
  @Column("transaction_description")
  String transactionDescription;
  @Column("previous_balance")
  BigDecimal previousBalance;
  @Column("current_balance")
  BigDecimal currentBalance;
  
}
