package com.example.cinemaai_telegrambot.config;

public class BotMessages {
    public static final String WELCOME_MESSAGE = """
            Добро пожаловать в *CinemaAI*!
            Я помогу вам спрогнозировать оценку фильма от кинокритиков.
            Для начала работы используйте команду */addmovie*, чтобы добавить данные о фильме.
            Список всех команд: */help*
            """;
    public static final String HELP_MESSAGE = """
            *Доступные команды:*
            */start* - _Начало работы с ботом_
            */help* - _Список всех команд_
            */addmovie* - _Добавить данные о фильме_
            */calculate* - _Рассчитать прогноз оценки_
            */back* - _Повторно ввести последний параметр рассчёта_
            */showdata* - _Просмотр введенных данных_
            */clear* - _Удалить форму для рассчёта_
            """;

    public static final String COMMAND_RECOGNIZED_ERROR = "Извините, но *команда не распознана*";

    public static final String CALCULATION_NOT_CREATED_YET_ERROR = "Извините, но *расчёт ещё не создан*.";

    public static final String CALCULATION_FORM_ALREADY_EXIST_ERROR = "*Форма для расчётов уже существует*. Для создания новой необходимо удалить старую */clear*";

    public static final String CLEAR_FORM_SUCCESS = "*Форма для расчётов успешно удалена*. Если хотите создать новую, используйте /addmovie";

    public static final String PLUG_IN_SHOW_DTO = "Пусто";

    public static final String FINAL_SCORE_TEXT = "По моим рассчётам, оценка кинокритиков - ";
}
