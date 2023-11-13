package com.example.tgMigratoria.service;

import com.example.tgMigratoria.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final Map<Long, UserState> userStates = new HashMap<>();
    private final Map<Long, UserContext> userContexts = new HashMap<>();
    private final MigrationStatusScrapper migrationStatusScrapper;

    public TelegramBot(BotConfig config, MigrationStatusScrapper migrationStatusScrapper) {
        this.config = config;
        this.migrationStatusScrapper = migrationStatusScrapper;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();

            UserState currentState = userStates.getOrDefault(chatId, UserState.START);

            if (currentState == UserState.START) {
                sendTextMessage(chatId, "Привет! Введите номер дела:");
                userStates.put(chatId, UserState.ENTER_DOCUMENT_NUMBER);
            } else if (currentState == UserState.ENTER_DOCUMENT_NUMBER) {
                String documentNumber = messageText.trim();
                sendTextMessage(chatId, "Теперь введите дату рождения в формате ДД:");
                userStates.put(chatId, UserState.ENTER_BIRTH_DAY);
                userContexts.computeIfAbsent(chatId, UserContext::new).setDocumentNumber(documentNumber);
            } else if (currentState == UserState.ENTER_BIRTH_DAY) {
                String birthDay = messageText.trim();
                sendTextMessage(chatId, "Теперь введите месяц рождения в формате ММ:");
                userStates.put(chatId, UserState.ENTER_BIRTH_MONTH);
                userContexts.get(chatId).setBirthDay(birthDay);
            } else if (currentState == UserState.ENTER_BIRTH_MONTH) {
                String birthMonth = messageText.trim();
                sendTextMessage(chatId, "Теперь введите год рождения в формате ГГГГ:");
                userStates.put(chatId, UserState.ENTER_BIRTH_YEAR);
                userContexts.get(chatId).setBirthMonth(birthMonth);
            } else if (currentState == UserState.ENTER_BIRTH_YEAR) {
                String birthYear = messageText.trim();
                userContexts.get(chatId).setBirthYear(birthYear);
                // Тут вызываем скраппер с использованием данных из UserContext
                String status = null;
                try {
                    status = migrationStatusScrapper.getStatusForUser(
                            userContexts.get(chatId).getDocumentNumber(),
                            userContexts.get(chatId).getBirthDay(),
                            userContexts.get(chatId).getBirthMonth(),
                            userContexts.get(chatId).getBirthYear()
                    );
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (status != null) {
                    sendTextMessage(chatId, "Статус вашего дела: " + status);
                } else {
                    sendTextMessage(chatId, "Информация не найдена.");
                }
                // Сбрасываем состояние пользователя
                userStates.remove(chatId);
                userContexts.get(chatId).reset();
            } else {
                throw new IllegalStateException("Недопустимое значение " + currentState);
            }
        }
    }

    private void sendTextMessage(long chatId, String text) {
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending message: " + e.getMessage());
        }
    }

    public enum UserState {
        START,
        ENTER_DOCUMENT_NUMBER,
        ENTER_BIRTH_DAY,
        ENTER_BIRTH_MONTH,
        ENTER_BIRTH_YEAR
    }
}