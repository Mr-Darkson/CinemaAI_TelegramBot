package com.example.cinemaai_telegrambot.service;

public interface MessageSender {
    void sendMessage(long chatId, String messageText);
}
