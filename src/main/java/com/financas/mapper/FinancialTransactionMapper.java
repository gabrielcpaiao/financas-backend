package com.financas.mapper;

import com.financas.domain.FinancialTransaction;
import com.financas.dto.request.FinancialTransactionRequest;
import com.financas.dto.response.FinancialTransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FinancialTransactionMapper {

    FinancialTransactionResponse toResponse(FinancialTransaction transaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "recurringTransactionId", ignore = true)
    @Mapping(target = "creditCardInstallmentId", ignore = true)
    @Mapping(target = "creditCardInvoiceId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FinancialTransaction toEntity(FinancialTransactionRequest request);
}