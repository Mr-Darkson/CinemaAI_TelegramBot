package com.example.cinemaai_telegrambot;

import com.example.cinemaai_telegrambot.config.BotMessages;
import com.example.cinemaai_telegrambot.domain.dto.CalculationShowDto;
import com.example.cinemaai_telegrambot.domain.entity.CalculationOrder;
import org.springframework.stereotype.Component;

@Component
public class CalculationMapper {

    public static CalculationShowDto toShowDto(CalculationOrder order) {
        String plug = BotMessages.PLUG_IN_SHOW_DTO;

        return CalculationShowDto.builder()
                .title(order != null ? order.getTitle() : plug)
                .year(order != null ? order.getYear() : plug)
                .durationInMinutes(order != null ? order.getDurationInMinutes() : plug)
                .ageRestriction(order != null ? order.getAgeRestriction() : plug)
                .budget(order != null ? order.getBudget() : plug)
                .genres(order != null ? order.getGenres() : plug)
                .country(order != null ? order.getCountry() : plug)
                .actors(order != null ? order.getActors() : plug)
                .director(order != null ? order.getDirector() : plug)
                .producer(order != null ? order.getProducer() : plug)
                .screenwriter(order != null ? order.getScreenwriter() : plug)
                .description(order != null ? order.getDescription() : plug)
                .build();
    }
}
