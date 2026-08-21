package com.financas.mapper;

import com.financas.domain.PlannedPurchase;
import com.financas.dto.request.PlannedPurchaseRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlannedPurchaseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "purchaseDate", ignore = true)
    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "creditCardPurchaseId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PlannedPurchase toEntity(PlannedPurchaseRequest request);
}