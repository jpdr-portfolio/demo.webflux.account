package com.jpdr.apps.demo.webflux.account.service.mapper;

import com.jpdr.apps.demo.webflux.account.model.AccountTransaction;
import com.jpdr.apps.demo.webflux.account.service.dto.account.AccountTransactionDto;
import com.jpdr.apps.demo.webflux.account.service.enums.AccountTransactionTypeEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", imports = {java.util.Objects.class, AccountTransactionTypeEnum.class})
public interface AccountTransactionMapper {
  
  AccountTransactionMapper INSTANCE = Mappers.getMapper(AccountTransactionMapper.class);
  
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "accountId", ignore = true)
  @Mapping(target = "transactionDate", ignore = true)
  @Mapping(target = "transactionDescription", ignore = true)
  @Mapping(target = "transactionType", expression = "java(dto.getTransactionType().getValue())" )
  @Mapping(target = "previousBalance", ignore = true)
  @Mapping(target = "currentBalance", ignore = true)
  AccountTransaction dtoToEntity(AccountTransactionDto dto);
  
  @Mapping(target = "transactionDate", expression = "java(Objects.toString(entity.getTransactionDate(),null))" )
  @Mapping(target = "transactionType", expression = "java(AccountTransactionTypeEnum.fromValue(entity.getTransactionType()))" )
  AccountTransactionDto entityToDto(AccountTransaction entity);
  
}
