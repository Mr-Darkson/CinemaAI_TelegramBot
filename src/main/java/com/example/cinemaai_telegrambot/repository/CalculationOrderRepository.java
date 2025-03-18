package com.example.cinemaai_telegrambot.repository;

import com.example.cinemaai_telegrambot.domain.entity.CalculationOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CalculationOrderRepository extends JpaRepository<CalculationOrder, UUID> {

    @Query("SELECT co FROM CalculationOrder co JOIN co.user u WHERE u.chatId = :chatId")
    Optional<CalculationOrder> findByChatId(@Param("chatId") Long chatId);

    @Modifying
    @Query(value = "DELETE FROM calculation_data WHERE id = ?1", nativeQuery = true)
    void deleteById(UUID id);
}
