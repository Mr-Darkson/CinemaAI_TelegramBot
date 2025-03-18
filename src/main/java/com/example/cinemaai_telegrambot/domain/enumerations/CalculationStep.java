package com.example.cinemaai_telegrambot.domain.enumerations;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CalculationStep {
    AWAITING_TITLE("Ожидание названия", "Введите название фильма"),
    AWAITING_YEAR("Ожидание года", "Введите год выпуска фильма"),
    AWAITING_DURATION("Ожидание длительности в минутах", "Введите длительность фильма в минутах"),
    AWAITING_AGE_RESTRICTION("Ожидание возрастного ограничения", "Введите возрастное ограничение (0+, 6+, 12+, 16+, 18+)"),
    AWAITING_BUDGET("Ожидание бюджета", "Введите бюджет фильма в $"),
    AWAITING_GENRE("Ожидание жанра", "Введите жанр фильма"),
    AWAITING_COUNTRY("Ожидание страны", "Введите страну производства фильма"),
    AWAITING_ACTORS("Ожидание актёров", "Перечислите актёров через запятую"),
    AWAITING_DIRECTOR("Ожидание режисёра", "Кто режисёр фильма?"),
    AWAITING_PRODUCER("Ожидание продюссера", "Кто продюссер фильма?"),
    AWAITING_SCREENWRITER("Ожидание сценариста", "Кто сценарист фильма?"),
    AWAITING_DESCRIPTION("Ожидание описания", "Введите описание фильма"),
    FINISH("Готово к рассчёту", "Данные собраны. Для запуска рассчёта введите /calculate");

    private final String name;
    private final String description;

    public CalculationStep nextStep() {
        CalculationStep[] steps = values();
        int nextOrdinal = this.ordinal() + 1;
        return nextOrdinal < steps.length ? steps[nextOrdinal] : steps[this.ordinal()];
    }

    public CalculationStep backStep() {
        CalculationStep[] steps = values();
        int nextOrdinal = this.ordinal() - 1;
        return nextOrdinal > 0 ? steps[nextOrdinal] : steps[this.ordinal()];
    }
}
