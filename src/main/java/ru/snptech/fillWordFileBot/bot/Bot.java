package ru.snptech.fillWordFileBot.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.snptech.fillWordFileBot.service.UserService;

import java.util.List;

@Component
public class Bot extends TelegramLongPollingBot {

    @Autowired
    private UserService service;

    private InlineKeyboardMarkup keyboardM1;
    private InlineKeyboardMarkup keyboardM2;
    private InlineKeyboardButton button1;
    private InlineKeyboardButton button2;
    public Bot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    @Override
    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
        var user = msg.getFrom();

        System.out.println(user.getFirstName() + " wrote " + msg.getText());
        button1 = InlineKeyboardButton.builder()
                .text("Мужской").callbackData("male")
                .build();
        button2 = InlineKeyboardButton.builder()
                .text("Женский").callbackData("female")
                .build();

        keyboardM1 = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(button1)).build();
        keyboardM2 = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(button1))
                .keyboardRow(List.of(button2))
                .build();

        sendMenu(update.getMessage().getChatId(), "Тест", keyboardM2);
    }

    /**
     * Выводит меню с выбором варианта ответа
     * @param who id пользователя
     * @param txt текст меню
     * @param kb Экземпляр класса InlineKeyboardMarkup, содержащий необходимые кнопки
     */
    public void sendMenu(Long who, String txt, InlineKeyboardMarkup kb) {
        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("HTML").text(txt)
                .replyMarkup(kb).build();

        try {
            execute(sm);
        } catch (TelegramApiException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
