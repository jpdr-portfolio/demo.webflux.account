package com.jpdr.apps.demo.webflux.account.service.mapper;

import com.jpdr.apps.demo.webflux.account.model.Account;
import com.jpdr.apps.demo.webflux.account.service.dto.account.AccountDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", imports = java.util.Objects.class)
public interface AccountMapper {
  
  AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);
  
  @Mapping(target = "id", ignore = true )
  @Mapping(target = "number", ignore = true )
  @Mapping(target = "ownerId", ignore = true )
  @Mapping(target = "ownerName", ignore = true )
  @Mapping(target = "balance", ignore = true )
  @Mapping(target = "lastTransactionDate", ignore = true )
  @Mapping(target = "lastTransactionId", ignore = true )
  @Mapping(target = "isActive", ignore = true)
  @Mapping(target = "creationDate", ignore = true)
  @Mapping(target = "deletionDate", ignore = true)
  Account dtoToEntity(AccountDto dto);
  
  @Mapping(target = "lastTransactionDate", expression = "java(Objects.toString(entity.getLastTransactionDate(),null))" )
  @Mapping(target = "creationDate", expression = "java(Objects.toString(entity.getCreationDate(),null))" )
  @Mapping(target = "deletionDate", expression = "java(Objects.toString(entity.getDeletionDate(),null))" )
  AccountDto entityToDto(Account entity);
  
}


