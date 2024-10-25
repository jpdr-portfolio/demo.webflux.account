package com.jpdr.apps.demo.webflux.account.exception.account;

public class AccountNotFoundException extends RuntimeException{
  
  public AccountNotFoundException(int id){
    super("The account " + id + " wasn't found.");
  }
  
}
