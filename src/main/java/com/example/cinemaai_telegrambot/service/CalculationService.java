package com.example.cinemaai_telegrambot.service;

import com.example.cinemaai_telegrambot.domain.entity.CalculationOrder;
import com.example.cinemaai_telegrambot.domain.entity.User;

public interface CalculationService {
    CalculationOrder getCurrentOrder(Long chatId);

    CalculationOrder createOrder(User user);

    void backStepParameter(CalculationOrder currentOrder);

    void nextStepParameter(CalculationOrder currentOrder);

    Boolean deleteOrderByChatId(Long chatId);

    String calculateScore(CalculationOrder currentOrder);
}



