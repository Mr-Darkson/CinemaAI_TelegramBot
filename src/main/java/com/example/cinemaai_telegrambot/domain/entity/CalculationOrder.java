package com.example.cinemaai_telegrambot.domain.entity;

import com.example.cinemaai_telegrambot.domain.enumerations.CalculationStep;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "calculation_data")

public class CalculationOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private CalculationStep step = CalculationStep.AWAITING_TITLE;

    @Column
    private String title;

    @Column
    private String year;

    @Column(name = "duration_in_minutes")
    private String durationInMinutes;

    @Column(name = "age_restriction")
    private String ageRestriction;

    @Column
    private String budget;

    @Column
    private String genres;

    @Column
    private String country;

    @Column
    private String actors;

    @Column
    private String director;

    @Column
    private String producer;

    @Column
    private String screenwriter;

    @Column
    private String description;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public CalculationOrder(User user) {
        this.user = user;
    }
}
