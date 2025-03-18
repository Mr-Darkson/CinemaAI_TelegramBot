package com.example.cinemaai_telegrambot.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CalculationRequestDto {
    @JsonProperty("year")
    private List<Integer> year;

    @JsonProperty("movieLength")
    private List<Integer> movieLength;

    @JsonProperty("ageRating")
    private List<Double> ageRating;

    @JsonProperty("budget")
    private List<Integer> budget;

    @JsonProperty("genres")
    private List<List<String>> genres;

    @JsonProperty("country")
    private List<String> country;

    @JsonProperty("actors")
    private List<List<String>> actors;

    @JsonProperty("writer")
    private List<String> writer;

    @JsonProperty("director")
    private List<String> director;

    @JsonProperty("producer")
    private List<String> producer;
}
