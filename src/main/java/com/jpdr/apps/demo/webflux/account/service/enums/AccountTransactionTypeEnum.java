package com.jpdr.apps.demo.webflux.account.service.enums;

import jakarta.validation.ValidationException;
import lombok.Getter;

@Getter
public enum AccountTransactionTypeEnum {
  
  DEBIT("D"),
  CREDIT("C");
  
  private final String value;
  
  AccountTransactionTypeEnum(String value){
    this.value = value;
  }
  
  public static AccountTransactionTypeEnum fromValue(String type){
    for(AccountTransactionTypeEnum enumType : AccountTransactionTypeEnum.values()){
      String valueType = enumType.getValue();
      if(valueType.equals(type)){
        return enumType;
      }
    }
    throw new ValidationException("Invalid account transaction type: " + type);
  }
  
}
