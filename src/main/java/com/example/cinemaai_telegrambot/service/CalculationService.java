package com.example.cinemaai_telegrambot.service;

import com.example.cinemaai_telegrambot.domain.entity.CalculationOrder;
import com.example.cinemaai_telegrambot.domain.entity.User;

public interface CalculationService {
    CalculationOrder getCurrentOrder(Long chatId);

    void createOrder(User user);

    void backStepParameter(CalculationOrder currentOrder);

    void nextStepParameter(CalculationOrder currentOrder);

    void clearForm(User user);
}



