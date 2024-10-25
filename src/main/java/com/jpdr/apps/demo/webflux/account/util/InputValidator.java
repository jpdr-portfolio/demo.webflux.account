package com.jpdr.apps.demo.webflux.account.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@Slf4j
public class InputValidator {
  
  private static final Pattern NAME_PATTERN = Pattern.compile("^[^\\[\\]\\%\\/\\=\\^\\<\\>\\#\\|]+$");
  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
  
  public static boolean isValidName(String name){
    if(StringUtils.isBlank(name) || !NAME_PATTERN.matcher(name).matches()){
      log.warn("Invalid name: {}",name);
      return false;
    }
    return true;
  }
  
  public static boolean isValidEmail(String email){
    if(StringUtils.isBlank(email) || !EMAIL_PATTERN.matcher(email).matches()){
      log.warn("Invalid email: {}",email);
      return false;
    }
    return true;
  }
  
  public static boolean isValidAmount(BigDecimal amount){
    if(amount.compareTo(BigDecimal.ZERO) < 1){
      log.warn("Invalid amount: {}",amount.toPlainString());
      return false;
    }
    return true;
  }
  
}
