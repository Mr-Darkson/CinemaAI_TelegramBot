package com.example.cinemaai_telegrambot.handler;

import com.example.cinemaai_telegrambot.config.BotMessages;
import com.example.cinemaai_telegrambot.domain.entity.CalculationOrder;
import com.example.cinemaai_telegrambot.domain.enumerations.CalculationStep;
import com.example.cinemaai_telegrambot.service.CalculationService;
import com.example.cinemaai_telegrambot.service.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageHandler {

    private final CalculationService calculationService;

    @Lazy private final MessageSender messageSender;

    public void handleMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText();
        String username = update.getMessage().getFrom().getUserName();

        CalculationOrder calculationOrder = calculationService.getCurrentOrder(chatId);

        if (!validateCalculationOrder(calculationOrder, chatId, username)) return;

        CalculationStep currentStep = calculationOrder.getStep();

        if (isCalculationFinished(currentStep, chatId)) return;

        handleCurrentStep(calculationOrder, currentStep, message);
        proceedToNextStep(calculationOrder, chatId);
    }

    /**
     * Проверяет, существует ли текущий заказ.
     * Если заказ отсутствует, отправляет сообщение об ошибке.
     */
    private boolean validateCalculationOrder(CalculationOrder calculationOrder, Long chatId, String username) {
        if (calculationOrder == null) {
            messageSender.sendMessage(chatId, BotMessages.COMMAND_RECOGNIZED_ERROR);
            log.error("Replied to user: {} Error: Command not recognized", username);
            return false;
        }
        return true;
    }

    /**
     * Проверяет, завершен ли процесс расчета.
     * Если завершен, отправляет сообщение пользователю.
     */
    private boolean isCalculationFinished(CalculationStep currentStep, Long chatId) {
        if (currentStep == CalculationStep.FINISH) {
            messageSender.sendMessage(chatId, currentStep.getDescription());
            return true;
        }
        return false;
    }

    /**
     * Обрабатывает текущий шаг и обновляет данные заказа.
     */
    private void handleCurrentStep(CalculationOrder calculationOrder, CalculationStep currentStep, String message) {
        switch (currentStep) {
            case AWAITING_TITLE -> calculationOrder.setTitle(message);
            case AWAITING_YEAR -> calculationOrder.setYear(message);
            case AWAITING_DURATION -> calculationOrder.setDurationInMinutes(message);
            case AWAITING_AGE_RESTRICTION -> calculationOrder.setAgeRestriction(message);
            case AWAITING_BUDGET -> calculationOrder.setBudget(message);
            case AWAITING_GENRE -> calculationOrder.setGenres(message);
            case AWAITING_COUNTRY -> calculationOrder.setCountry(message);
            case AWAITING_ACTORS -> calculationOrder.setActors(message);
            case AWAITING_DIRECTOR -> calculationOrder.setDirector(message);
            case AWAITING_PRODUCER -> calculationOrder.setProducer(message);
            case AWAITING_SCREENWRITER -> calculationOrder.setScreenwriter(message);
            case AWAITING_DESCRIPTION -> calculationOrder.setDescription(message);
        }
    }

    /**
     * Переходит к следующему шагу и отправляет сообщение пользователю.
     */
    private void proceedToNextStep(CalculationOrder calculationOrder, Long chatId) {
        calculationService.nextStepParameter(calculationOrder);
        messageSender.sendMessage(chatId, calculationOrder.getStep().getDescription());
    }
}
