package com.example.cinemaai_telegrambot.service.impl;

import com.example.cinemaai_telegrambot.domain.entity.User;
import com.example.cinemaai_telegrambot.repository.UserRepository;
import com.example.cinemaai_telegrambot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public void createUser(Long chatId, String username) {
        User user = new User(chatId, username);
        userRepository.save(user);
    }

    @Override
    public User findUserByChatId(Long chatId) {
        return userRepository.findByChatId(chatId)
                .orElse(null);
    }
}
