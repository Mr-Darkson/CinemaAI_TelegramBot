package com.example.cinemaai_telegrambot.service.impl;

import com.example.cinemaai_telegrambot.domain.entity.CalculationOrder;
import com.example.cinemaai_telegrambot.domain.entity.User;
import com.example.cinemaai_telegrambot.domain.enumerations.CalculationStep;
import com.example.cinemaai_telegrambot.repository.CalculationOrderRepository;
import com.example.cinemaai_telegrambot.service.CalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalculationServiceImpl implements CalculationService {

    private final CalculationOrderRepository calculationOrderRepository;

    @Override
    public CalculationOrder getCurrentOrder(Long chatId) {
        return calculationOrderRepository
                .findByChatId(chatId)
                .orElse(null);
    }

    @Override
    public void createOrder(User owner) {
        calculationOrderRepository
                .save(new CalculationOrder(owner));
    }

    @Override
    public void backStepParameter(CalculationOrder currentOrder) {
        CalculationStep backStep = currentOrder.getStep().backStep();
        currentOrder.setStep(backStep);
        calculationOrderRepository.save(currentOrder);
    }

    @Override
    public void nextStepParameter(CalculationOrder calculationOrder) {
        CalculationStep nextStep = calculationOrder.getStep().nextStep();
        calculationOrder.setStep(nextStep);
        calculationOrderRepository.save(calculationOrder);
    }

    @Override
    public void clearForm(User user) {
        calculationOrderRepository.deleteByUser(user);
    }
}
