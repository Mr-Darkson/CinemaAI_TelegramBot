package com.example.cinemaai_telegrambot.handler;

import com.example.cinemaai_telegrambot.util.CalculationMapper;
import com.example.cinemaai_telegrambot.config.BotMessages;
import com.example.cinemaai_telegrambot.domain.dto.CalculationShowDto;
import com.example.cinemaai_telegrambot.domain.entity.CalculationOrder;
import com.example.cinemaai_telegrambot.domain.entity.User;
import com.example.cinemaai_telegrambot.domain.enumerations.CalculationStep;
import com.example.cinemaai_telegrambot.service.CalculationService;
import com.example.cinemaai_telegrambot.service.MessageSender;
import com.example.cinemaai_telegrambot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TelegramBotHandler extends TelegramLongPollingBot implements MessageSender {

    private final CalculationService calculationService;
    private final UserService userService;

    @Value("${bot.name}")
    private String botName;

    public TelegramBotHandler(@Value("${bot.token}") String token,
                              CalculationService calculationService,
                              UserService userService) {
        super(token);
        this.calculationService = calculationService;
        this.userService = userService;
        buildCommandList();
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();

            if  (message.startsWith("/")) {
                log.info("Message received: {}. Recognized as command", message);
                handleCommand(update);
            }
            else {
                log.info("Message received: {}. Recognized as parameter", message);
                handleMessage(update);
            }

        }
    }

    @Override
    public void sendMessage(long chatId, String messageText) {
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId);
        message.setText(messageText);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    private void buildCommandList() {
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/help", "show help information"));
        listOfCommands.add(new BotCommand("/addmovie", "add data about the film"));
        listOfCommands.add(new BotCommand("/showdata", "show form data"));
        listOfCommands.add(new BotCommand("/back", "re-enter the last calculation parameter"));
        listOfCommands.add(new BotCommand("/calculate", "calculate the estimate forecast"));
        listOfCommands.add(new BotCommand("/clear", "clear movie prediction data"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

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
            sendMessage(chatId, BotMessages.COMMAND_RECOGNIZED_ERROR);
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
            sendMessage(chatId, currentStep.getDescription());
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
        sendMessage(chatId, calculationOrder.getStep().getDescription());
    }


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

            case "/back" -> {
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

            case "/calculate" -> {
                calculateChance(chatId);
                log.info("Replied to user: {} Command: Calculate", username);
            }

            default -> {
                sendMessage(chatId, BotMessages.COMMAND_RECOGNIZED_ERROR);
                log.error("Replied to user: {} Error: Command not recognized", username);
            }
        }
    }



    private void startCommandReceived(long chatId, String username) {
        if(userService.findUserByChatId(chatId) == null) {
            userService.createUser(chatId, username);
        }
        sendMessage(chatId, BotMessages.WELCOME_MESSAGE);
    }

    private void helpCommandReceived(long chatId) {
        sendMessage(chatId, BotMessages.HELP_MESSAGE);
    }


    private void addMovieCommandReceived(long chatId) {
        CalculationOrder currentOrder = calculationService.getCurrentOrder(chatId);
        if (currentOrder != null) {
            sendMessage(chatId, BotMessages.CALCULATION_FORM_ALREADY_EXIST_ERROR);
            return;
        }
        User user = userService.findUserByChatId(chatId);

        calculationService.createOrder(user);
        sendMessage(chatId, CalculationStep.AWAITING_TITLE.getDescription());
    }

    private void backCommandReceived(long chatId) {
        CalculationOrder currentOrder = calculationService.getCurrentOrder(chatId);
        if (currentOrder == null) {
            sendMessage(chatId, BotMessages.CALCULATION_NOT_CREATED_YET_ERROR);
            return;
        }

        calculationService.backStepParameter(currentOrder);
        sendMessage(chatId, currentOrder.getStep().getDescription());

    }

    private void showDataCommandReceived(long chatId) {
        CalculationOrder currentOrder = calculationService.getCurrentOrder(chatId);
        if (currentOrder == null) {
            sendMessage(chatId, BotMessages.CALCULATION_NOT_CREATED_YET_ERROR);
            return;
        }

        CalculationShowDto showDto = CalculationMapper.toShowDto(currentOrder);
        sendMessage(chatId, showDto.toString());
    }

    public void clearCommandReceived(long chatId) {
        if(calculationService.deleteOrderByChatId(chatId)) {
            sendMessage(chatId, BotMessages.CLEAR_FORM_SUCCESS);
        }
        else sendMessage(chatId, BotMessages.CALCULATION_NOT_CREATED_YET_ERROR);
    }

    private void calculateChance(Long chatId) {
        CalculationOrder currentOrder = calculationService.getCurrentOrder(chatId);
        if (currentOrder == null) {
            sendMessage(chatId, BotMessages.CALCULATION_NOT_CREATED_YET_ERROR);
        }
        else {
            String score = calculationService.calculateScore(currentOrder);
            String messageWithScore = BotMessages.FINAL_SCORE_TEXT + score;
            sendMessage(chatId, messageWithScore);
        }
    }
}
