package com.example.cinemaai_telegrambot.repository;

import com.example.cinemaai_telegrambot.domain.entity.CalculationOrder;
import com.example.cinemaai_telegrambot.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CalculationOrderRepository extends JpaRepository<CalculationOrder, UUID> {

    @Query("SELECT co FROM CalculationOrder co JOIN co.user u WHERE u.chatId = :chatId")
    Optional<CalculationOrder> findByChatId(@Param("chatId") Long chatId);

    void deleteByUser(User user);
}
