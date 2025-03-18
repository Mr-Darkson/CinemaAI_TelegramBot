package com.example.cinemaai_telegrambot.handler;

import com.example.cinemaai_telegrambot.CalculationMapper;
import com.example.cinemaai_telegrambot.config.BotMessages;
import com.example.cinemaai_telegrambot.domain.dto.CalculationShowDto;
import com.example.cinemaai_telegrambot.domain.entity.CalculationOrder;
import com.example.cinemaai_telegrambot.domain.entity.User;
import com.example.cinemaai_telegrambot.domain.enumerations.CalculationStep;
import com.example.cinemaai_telegrambot.service.CalculationService;
import com.example.cinemaai_telegrambot.service.MessageSender;
import com.example.cinemaai_telegrambot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommandHandler {

    private final UserService userService;
    private final CalculationService calculationService;
    @Lazy
    private final MessageSender messageSender;

    public void handleCommand(Update update) {
        Long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText();
        String username = update.getMessage().getFrom().getUserName();

        switch (message) {
            case "/start" -> {
                startCommandReceived(chatId, username);
                log.info("Replied to user: {} Command: Start", username);
            }

            case "/help" -> {
                helpCommandReceived(chatId);
                log.info("Replied to user: {} Command: Help", username);
            }

            case "/addmovie" -> {
                addMovieCommandReceived(chatId);
                log.info("Replied to user: {} Command: Add movie", username);
            }

            case "/cancel" -> {
                backCommandReceived(chatId);
                log.info("Replied to user: {} Command: Cancel", username);
            }

            case "/showdata" -> {
                showDataCommandReceived(chatId);
                log.info("Replied to user: {} Command: Show data", username);
            }

            case "/clear" -> {
                clearCommandReceived(chatId);
                log.info("Replied to user: {} Command: Clear", username);
            }

            default -> {
                messageSender.sendMessage(chatId, BotMessages.COMMAND_RECOGNIZED_ERROR);
                log.error("Replied to user: {} Error: Command not recognized", username);
            }
        }
    }

    private void startCommandReceived(long chatId, String username) {
        userService.createUser(chatId, username);
        messageSender.sendMessage(chatId, BotMessages.WELCOME_MESSAGE);
    }

    private void helpCommandReceived(long chatId) {
        messageSender.sendMessage(chatId, BotMessages.HELP_MESSAGE);
    }

    private void addMovieCommandReceived(long chatId) {
        CalculationOrder currentOrder = calculationService.getCurrentOrder(chatId);
        if (currentOrder != null) {
            messageSender.sendMessage(chatId, BotMessages.CALCULATION_FORM_ALREADY_EXIST_ERROR);
            return;
        }
        User user = userService.findUserByChatId(chatId);

        calculationService.createOrder(user);
        messageSender.sendMessage(chatId, CalculationStep.AWAITING_TITLE.getDescription());
    }

    private void backCommandReceived(long chatId) {
        CalculationOrder currentOrder = calculationService.getCurrentOrder(chatId);
        if (currentOrder == null) {
            messageSender.sendMessage(chatId, BotMessages.CALCULATION_NOT_CREATED_YET_ERROR);
            return;
        }

        calculationService.backStepParameter(currentOrder);
        messageSender.sendMessage(chatId, currentOrder.getStep().getDescription());

    }

    private void showDataCommandReceived(long chatId) {
        CalculationOrder currentOrder = calculationService.getCurrentOrder(chatId);
        if (currentOrder == null) messageSender.sendMessage(chatId, BotMessages.CALCULATION_NOT_CREATED_YET_ERROR);

        CalculationShowDto showDto = CalculationMapper.toShowDto(currentOrder);
        messageSender.sendMessage(chatId, showDto.toString());
    }

    public void clearCommandReceived(long chatId) {
        User user = userService.findUserByChatId(chatId);
        calculationService.clearForm(user);
        messageSender.sendMessage(chatId, BotMessages.CLEAR_FORM_SUCCESS);
    }
}
