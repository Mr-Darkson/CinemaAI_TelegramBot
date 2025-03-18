package com.example.cinemaai_telegrambot.service;

import com.example.cinemaai_telegrambot.domain.entity.User;

public interface UserService {

    void createUser(Long chatId, String username);

    User findUserByChatId(Long chatId);
}
