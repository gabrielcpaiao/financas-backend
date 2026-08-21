package com.financas.mapper;

import com.financas.domain.CreditCard;
import com.financas.dto.request.CreditCardRequest;
import com.financas.dto.response.CreditCardResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CreditCardMapper {

    CreditCardResponse toResponse(CreditCard creditCard);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CreditCard toEntity(CreditCardRequest request);
}