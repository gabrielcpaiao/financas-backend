package com.financas.mapper;

import com.financas.domain.Investment;
import com.financas.dto.request.InvestmentRequest;
import com.financas.dto.response.InvestmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvestmentMapper {

    @Mapping(target = "totalContributed", ignore = true)
    InvestmentResponse toResponse(Investment investment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Investment toEntity(InvestmentRequest request);
}