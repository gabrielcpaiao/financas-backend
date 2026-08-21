package com.financas.service.impl;

import com.financas.domain.CreditCard;
import com.financas.dto.request.CreditCardRequest;
import com.financas.dto.response.CreditCardResponse;
import com.financas.exception.ResourceNotFoundException;
import com.financas.mapper.CreditCardMapper;
import com.financas.repository.CreditCardRepository;
import com.financas.service.CreditCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditCardServiceImpl implements CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final CreditCardMapper creditCardMapper;

    @Override
    public List<CreditCardResponse> listActive(Long userId) {
        return creditCardRepository.findByUserIdAndActiveTrue(userId).stream()
                .map(creditCardMapper::toResponse)
                .toList();
    }

    @Override
    public CreditCardResponse create(Long userId, CreditCardRequest request) {
        CreditCard creditCard = creditCardMapper.toEntity(request);
        creditCard.setUserId(userId);
        creditCard.setActive(true);
        return creditCardMapper.toResponse(creditCardRepository.save(creditCard));
    }

    @Override
    public CreditCardResponse update(Long userId, Long id, CreditCardRequest request) {
        CreditCard creditCard = findOwnedOrThrow(userId, id);
        creditCard.setName(request.name());
        creditCard.setBrand(request.brand());
        creditCard.setCreditLimit(request.creditLimit());
        creditCard.setClosingDay(request.closingDay());
        creditCard.setDueDay(request.dueDay());
        return creditCardMapper.toResponse(creditCardRepository.save(creditCard));
    }

    @Override
    public void deactivate(Long userId, Long id) {
        // Nunca DELETE de verdade: cartão pode estar referenciado por
        // credit_card_purchase/credit_card_invoice (ON DELETE RESTRICT/CASCADE no schema).
        CreditCard creditCard = findOwnedOrThrow(userId, id);
        creditCard.setActive(false);
        creditCardRepository.save(creditCard);
    }

    private CreditCard findOwnedOrThrow(Long userId, Long id) {
        return creditCardRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cartão não encontrado: " + id));
    }
}
