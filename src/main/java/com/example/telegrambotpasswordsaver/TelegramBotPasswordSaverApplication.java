package com.example.telegrambotpasswordsaver;

import com.example.telegrambotpasswordsaver.Controllers.BotController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.sql.SQLException;

@SpringBootApplication
public class TelegramBotPasswordSaverApplication {

    public static void main(String[] args) {
        TelegramBotsApi botsApi;
        try {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new BotController());
        } catch (TelegramApiException | SQLException e) {
            throw new RuntimeException(e);
        }


    }

}
