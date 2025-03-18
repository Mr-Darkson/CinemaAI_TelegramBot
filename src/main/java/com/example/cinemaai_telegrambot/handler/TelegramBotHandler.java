package com.example.cinemaai_telegrambot.handler;

import com.example.cinemaai_telegrambot.service.CalculationService;
import com.example.cinemaai_telegrambot.service.MessageSender;
import com.example.cinemaai_telegrambot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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

    @Value("${bot.name}")
    private String botName;
    private final CommandHandler commandHandler;
    private final MessageHandler messageHandler;

    public TelegramBotHandler(@Value("${bot.token}") String token,
                              CommandHandler commandHandler, MessageHandler messageHandler) {
        super(token);
        this.messageHandler = messageHandler;
        this.commandHandler = commandHandler;
        buildCommandList();
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();

            if (message.startsWith("/")) commandHandler.handleCommand(update);
            else messageHandler.handleMessage(update);
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
        listOfCommands.add(new BotCommand("/calculate", "calculate the estimate forecast"));
        listOfCommands.add(new BotCommand("/back", "Re-enter the last calculation parameter"));
        listOfCommands.add(new BotCommand("/cancel", "viewing the entered data"));
        listOfCommands.add(new BotCommand("/clear", "clear movie prediction data"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
