package com.example.cinemaai_telegrambot.util;

import com.example.cinemaai_telegrambot.config.BotMessages;
import com.example.cinemaai_telegrambot.domain.dto.CalculationRequestDto;
import com.example.cinemaai_telegrambot.domain.dto.CalculationShowDto;
import com.example.cinemaai_telegrambot.domain.entity.CalculationOrder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    public static CalculationRequestDto toRequestDto(CalculationOrder order) {
        CalculationRequestDto dto = new CalculationRequestDto();

        // Преобразование полей
        dto.setYear(Collections.singletonList(Integer.parseInt(order.getYear())));
        dto.setMovieLength(Collections.singletonList(Integer.parseInt(order.getDurationInMinutes())));
        dto.setAgeRating(Collections.singletonList(Double.parseDouble(order.getAgeRestriction().replaceAll("\\+", ""))));
        dto.setBudget(Collections.singletonList(Integer.parseInt(order.getBudget())));

        // Преобразование строки жанров в список
        dto.setGenres(Collections.singletonList(Arrays.asList(order.getGenres().toLowerCase().split(", "))));

        dto.setCountry(Collections.singletonList(order.getCountry()));

        // Преобразование строки актеров в список и капитализация имен
        List<String> actors = Arrays.asList(order.getActors().split(", "));
        List<String> capitalizedActors = actors.stream()
                .map(CalculationMapper::capitalizeName)
                .toList();
        dto.setActors(Collections.singletonList(capitalizedActors));

        // Капитализация имен продюсера, режиссера и сценариста
        dto.setWriter(Collections.singletonList(capitalizeName(order.getScreenwriter())));
        dto.setDirector(Collections.singletonList(capitalizeName(order.getDirector())));
        dto.setProducer(Collections.singletonList(capitalizeName(order.getProducer())));

        return dto;
    }

    // Метод для капитализации имени и фамилии
    private static String capitalizeName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        String[] parts = name.split(" ");
        StringBuilder capitalizedName = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                capitalizedName.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return capitalizedName.toString().trim();
    }
}
