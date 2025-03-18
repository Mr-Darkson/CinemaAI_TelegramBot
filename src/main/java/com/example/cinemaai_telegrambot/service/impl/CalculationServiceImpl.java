package com.example.cinemaai_telegrambot.service.impl;

import com.example.cinemaai_telegrambot.domain.dto.CalculationRequestDto;
import com.example.cinemaai_telegrambot.domain.dto.PredictionResponse;
import com.example.cinemaai_telegrambot.domain.entity.CalculationOrder;
import com.example.cinemaai_telegrambot.domain.entity.User;
import com.example.cinemaai_telegrambot.domain.enumerations.CalculationStep;
import com.example.cinemaai_telegrambot.repository.CalculationOrderRepository;
import com.example.cinemaai_telegrambot.service.CalculationService;
import com.example.cinemaai_telegrambot.util.CalculationMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalculationServiceImpl implements CalculationService {

    private final CalculationOrderRepository calculationOrderRepository;
    private final EntityManager entityManager;

    @Override
    public CalculationOrder getCurrentOrder(Long chatId) {
        return calculationOrderRepository
                .findByChatId(chatId)
                .orElse(null);
    }

    @Override
    public CalculationOrder createOrder(User owner) {

        CalculationOrder order = calculationOrderRepository
                .save(new CalculationOrder(owner));
        return order;

    }

    @Override
    public void backStepParameter(CalculationOrder currentOrder) {
        CalculationStep backStep = currentOrder.getStep().backStep();
        currentOrder.setStep(backStep);
        calculationOrderRepository.save(currentOrder);
    }

    @Override
    public void nextStepParameter(CalculationOrder calculationOrder) {
        CalculationStep nextStep = calculationOrder.getStep().nextStep();
        calculationOrder.setStep(nextStep);
        calculationOrderRepository.save(calculationOrder);
    }


    @Transactional
    @Override
    public Boolean deleteOrderByChatId(Long chatId) {
        CalculationOrder currentOrder = getCurrentOrder(chatId);
        if (currentOrder == null) {
            return false;
        }

        calculationOrderRepository.deleteById(currentOrder.getId());
        return true;
    }

    @SneakyThrows
    @Override
    public String calculateScore(CalculationOrder currentOrder) {
        String path = "http://localhost:5000/predict";
        CalculationRequestDto requestDto = CalculationMapper.toRequestDto(currentOrder);
        RestTemplate restTemplate = new RestTemplate();

        // Устанавливаем заголовки
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Создаем HTTP-запрос с DTO в теле
        HttpEntity<CalculationRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);

        ObjectMapper objectMapper = new ObjectMapper();
        log.info(objectMapper.writeValueAsString(requestDto));

        // Отправляем POST-запрос и получаем ответ
        ResponseEntity<PredictionResponse> responseEntity = restTemplate.exchange(
                path,
                HttpMethod.POST,
                requestEntity,
                PredictionResponse.class
        );



        // Проверяем статус ответа
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            double prediction = Objects.requireNonNull(responseEntity.getBody()).getPrediction();

            // Форматируем число до одной десятой
            DecimalFormat df = new DecimalFormat("#.#");
            df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US)); // Убедимся, что используется точка как разделитель

            return df.format(prediction); // Возвращаем отформатированное значение
        } else {
            log.error("Ошибка при отправке запроса: {}", responseEntity.getStatusCode());
            return "?. Произошла какая-то ошибка в рассчётах";
        }
    }
}
