package com.example.cinemaai_telegrambot.repository;

import com.example.cinemaai_telegrambot.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByChatId(Long chatId);
}
