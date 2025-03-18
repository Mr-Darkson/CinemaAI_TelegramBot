package com.example.cinemaai_telegrambot.domain.dto;

import lombok.Builder;

@Builder
public class CalculationShowDto {

    private String title;

    private String year;

    private String durationInMinutes;

    private String ageRestriction;

    private String budget;

    private String genres;

    private String country;

    private String actors;

    private String director;

    private String producer;

    private String screenwriter;

    private String description;

    @Override
    public String toString() {
        return String.format("""
                Название фильма: %s
                Год производства: %s
                Длительность в минутах: %s
                Возрастное ограничение: %s
                Бюджет: %s
                Жанр: %s
                Страна производства: %s
                Актёры: %s
                Режисёр: %s
                Продюссер: %s
                Сценарист: %s
                Описание:
                %s
                """, title, year, durationInMinutes, ageRestriction, budget, genres, country, actors, director, producer, screenwriter, description);
    }
}
