package com.example.telegrambotpasswordsaver.Controllers;

import com.example.telegrambotpasswordsaver.Components.Buttons;
import com.example.telegrambotpasswordsaver.Components.LIST_OF_COMMANDS;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.description.SetMyDescription;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.example.telegrambotpasswordsaver.Components.LIST_OF_COMMANDS;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class BotController extends TelegramLongPollingBot {

    final String helpText = """
                             
                              This bot can store, wipe and show your login and password.
                              
                              You can operate via menu or buttons.
                                 
                              Wipe all data --- '/wipeall'.
                              
                              Get buttons --- '/start' 
                              
                              Type '/save <NAME> <login> <password>' to store info.
                            """;

    final private String url = "xx";

    final private String username = "xx";

    final private String password = "xx";

    final private Connection connect = DriverManager.getConnection(url, username, password);

    @SneakyThrows
    public BotController() throws SQLException {


        List<BotCommand> commands = new ArrayList<>();


        commands.add(new BotCommand(LIST_OF_COMMANDS.START.getCommand(), LIST_OF_COMMANDS.START.getDescription()));
        commands.add(new BotCommand(LIST_OF_COMMANDS.STORE.getCommand(), LIST_OF_COMMANDS.STORE.getDescription()));
        commands.add(new BotCommand(LIST_OF_COMMANDS.HELP.getCommand(), LIST_OF_COMMANDS.HELP.getDescription()));
        commands.add(new BotCommand(LIST_OF_COMMANDS.SHOW_ALL.getCommand(), LIST_OF_COMMANDS.SHOW_ALL.getDescription()));
        commands.add(new BotCommand(LIST_OF_COMMANDS.WIPE_ALL.getCommand(), LIST_OF_COMMANDS.WIPE_ALL.getDescription()));

        SetMyCommands setMyCommands = new SetMyCommands();
        setMyCommands.setCommands(commands);

        SetMyDescription setMyDescription = new SetMyDescription();
        setMyDescription.setDescription("I can store your login and password");

        try {
            execute(setMyCommands);
            execute(setMyDescription);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            Long chatId = update.getMessage().getChatId();

            if (update.getMessage().getText().contains("/save")) {
                String str = update.getMessage().getText();
                String [] arr = str.split(" ");
                if(arr.length == 4) {
                    dataSaver(arr); //message String to array inserted
                    sendMessage(chatId,"DATA SUCCESSFULLY STORED");
                }
                else sendMessage(chatId, "WRONG DATA INPUT, TRY /help");

            }

            switch (update.getMessage().getText()) {
                case "/start": {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setReplyMarkup(Buttons.inlineMarkup());
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("CHOOSE OPTION:");
                    execute(sendMessage);
                    break;
                }

                case "/store": {
                    sendMessage(chatId, "Type '/save <NAME> <login> <password> to store info");
                }

                case "/help": {
                    sendMessage(chatId, helpText);
                    break;


                }

                case "/showall": {

                    if(isTableNotEmpty()) showAll(chatId);
                    else sendMessage(chatId, "YOUR DATA IS EMPTY");
                    break;

                }

                case "/wipeall": {
                    if(isTableNotEmpty()) wipeAll(chatId);
                    else sendMessage(chatId, "YOUR DATA IS ALREADY EMPTY");
                    break;
                }
            }




        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String callbackID = update.getCallbackQuery().getId();

            if (callbackData.equals("/store")) {

                sendMessage(chatId, "Type '/save <NAME> <login> <password> to store info");
                callBackQueryHelper(callbackID);

            }

            else if (callbackData.equals("/help")) {
                callBackQueryHelper(update.getCallbackQuery().getId());
                sendMessage(chatId, helpText);
            }

            else if (callbackData.equals("/showall")){
                if(isTableNotEmpty()) showAll(chatId);
                else sendMessage(chatId, "YOUR DATA IS EMPTY");
                callBackQueryHelper(callbackID);
            }

            else if(callbackData.equals("/wipeall")){
                if(isTableNotEmpty()) wipeAll(chatId);
                else sendMessage(chatId, "DATA IS ALREADY EMPTY");
                callBackQueryHelper(callbackID);
            }
        }
    }


    @SneakyThrows
    private void callBackQueryHelper(String id){
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(id);
        execute(answerCallbackQuery);
    }

    @SneakyThrows
    private void wipeAll(long chatId) {
        String sqlTruncateQuery = "TRUNCATE table `users`";
        Statement statement = connect.createStatement();
        statement.executeUpdate(sqlTruncateQuery);

        connect.setAutoCommit(false);
        connect.commit();
        sendMessage(chatId, "ALL DATA DELETED");

    }

    public void sendMessage(Long chatId, String message) {

        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setText(message);
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    @SneakyThrows
    public void dataSaver(String [] strDataArr) {
        PreparedStatement preparedStatement;
        String sqlQuery = "INSERT INTO users (NAME , login, password) VALUES (? , ? , ?)";
        preparedStatement = connect.prepareStatement(sqlQuery);

        for (int param = 1, arrCounter = 1; arrCounter<4 ; arrCounter++, param++){

            preparedStatement.setString(param, strDataArr[arrCounter]);


        }

        preparedStatement.executeUpdate();

    }

    public void showAll(Long chatId){
        try {
            String sqlQuery = "SELECT * from `users`";
            Statement statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            String data = "";

                while (resultSet.next()) {
                    data = data.concat(resultSet.getString("NAME") + "\t" + resultSet.getString("login")
                            + "\t" + resultSet.getString("password") + "\n\n");

                }
                sendMessage(chatId, data);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public boolean isTableNotEmpty(){
        String sqlQuery = "SELECT * from `users`";
        Statement statement = connect.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        return resultSet.next();

    }

    @Override
    public String getBotUsername() {
        return "MyProjeсtPasswordSaver";
    }

    @Override
    public String getBotToken() {
        return "12345678qwerty";
    }
}
