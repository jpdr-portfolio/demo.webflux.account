package com.jpdr.apps.demo.webflux.account.exception.account;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException{
  
  public InsufficientFundsException(BigDecimal amount){
    super("Insufficient funds to proceed with transaction. Current balance is " + amount.toPlainString());
  }
  
}
