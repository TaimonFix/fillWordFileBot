package ru.snptech.fillWordFileBot.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.snptech.fillWordFileBot.dto.User;
import ru.snptech.fillWordFileBot.service.UserService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Класс, реализующий логику бота FillWordFileBot
 */
@Component
public class Bot extends TelegramLongPollingBot {

    private final String START = "/start";
    private final String FILL_SURNAME_TEXT = "Введите фамилию:";
    private final String FILL_NAME_TEXT = "Введите имя:";
    private final String FILL_PATRONYMIC_TEXT = "Введите отчество:";
    private final String FILL_BIRTH_DATE_TEXT = "Введите дату рождения (Пример: 01.01.2000)";
    private final String FILL_GENDER_TEXT = "Укажите пол:";
    private final String FILL_PHOTO_TEXT = "Пришлите свое фото:";
    private final String OUTPUT_FILE_TEXT = "Данные собраны. Сейчас будет сформирован .docx файл.";
    private final String ERROR_BIRTH_DATE_TEXT = "Введите дату в формате dd.MM.YYYY (Пример: 01.01.2000)";

    @Autowired
    private UserService service;

    private InlineKeyboardMarkup keyboardM1;
    private InlineKeyboardButton button1;
    private InlineKeyboardButton button2;
    private String textButton1;
    private String textButton2;
    private String responseText;
    private User user1;

    public Bot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && (update.getMessage().hasText() || update.getMessage().hasPhoto())) {
            var msg = update.getMessage();
            var user = msg.getFrom();
            System.out.println(user.getFirstName() + " wrote " + msg.getText());

            if (msg.hasText() && msg.getText().startsWith(START)) {
                String[] parts = msg.getText().split(" ");
                String utm = parts.length > 1 ? parts[1] : null;
                user1 = new User();
                user1.setId((msg.getFrom().getId()));
                user1.setUtm(utm);
                startBot(update);
            } else {
                if (responseText != null) {
                    switch (responseText) {
                        case (FILL_SURNAME_TEXT):
                            fillSurname(update);
                            break;
                        case (FILL_NAME_TEXT):
                            fillName(update);
                            break;
                        case (FILL_PATRONYMIC_TEXT):
                            fillPatronymic(update);
                            break;
                        case (FILL_BIRTH_DATE_TEXT):
                            fillBirthDate(update);
                            break;
                        case (FILL_PHOTO_TEXT):
                            fillPhoto(update);
                            break;
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
            var callbackData = update.getCallbackQuery().getData();
            if (callbackData.equals("done")) {
                fillForm(update);
            } else if (callbackData.equals("Мужской") || callbackData.equals("Женский")) {
                fillGender(update);
            }
        }
    }

    /**
     * Отправка сообщения пользователю
     * @param who id пользователя
     * @param what сообщение
     */
    private void sendMessage(Long who, String what){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Выводит меню с выбором варианта ответа
     * @param who id пользователя
     * @param txt текст меню
     * @param kb Экземпляр класса InlineKeyboardMarkup, содержащий необходимые кнопки
     */
    private void sendMenu(Long who, String txt, InlineKeyboardMarkup kb) {
        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("HTML").text(txt)
                .replyMarkup(kb).build();

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendDocument(Long who, java.io.File file) {
        SendDocument sd = SendDocument.builder()
                    .chatId(who.toString())
                    .document(new InputFile(file))
                    .build();

        try {
            execute(sd);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Запуск логики бота
     */
    private void startBot(Update update) {
        responseText = "Привет! Перед началом работы необходимо дать согласие" +
                " на обработку персональных данных";
        sendMessage(update.getMessage().getChatId(), responseText);
        textButton1 = "Ссылка";
        textButton2 = "Принять ✅";
        String txt = "Согласие на обработку персональных данных";
        createPersonalDataMenu();
        sendMenu(update.getMessage().getChatId(), txt, keyboardM1);
    }


    /**
     *  Начало заполнение формы пользователя
     */
    private void fillForm(Update update) {
        responseText = "Отлично! Предлагаю заполнить информацию о себе:\n" + FILL_SURNAME_TEXT;
        sendMessage(update.getCallbackQuery().getMessage().getChatId(), responseText);
        responseText = FILL_SURNAME_TEXT;
    }

    /**
     *  Заполнение фамилии с последующей отправкой в базу данных
     */
    private void fillSurname(Update update) {
        user1.setSurname(update.getMessage().getText());
        responseText = FILL_NAME_TEXT;
        sendMessage(update.getMessage().getChatId(), responseText);
    }

    /**
     *  Заполнение имени с последующей отправкой в базу данных
     */
    private void fillName(Update update) {
        user1.setName(update.getMessage().getText());
        responseText = FILL_PATRONYMIC_TEXT;
        sendMessage(update.getMessage().getChatId(), responseText);
    }

    /**
     *  Заполнение отчества с последующей отправкой в базу данных
     */
    private void fillPatronymic(Update update) {
        user1.setPatronymic(update.getMessage().getText());
        responseText = FILL_BIRTH_DATE_TEXT;
        sendMessage(update.getMessage().getChatId(), responseText);
        System.out.println(user1.toString());
    }

    /**
     *  Заполнение даты рождения с последующей отправкой в базу данных
     * @throws DateTimeParseException при неправильном вводе даты
     */
    private void fillBirthDate(Update update) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        try {
            LocalDate date = LocalDate.parse(update.getMessage().getText(), dateFormat);
            user1.setBirthDate(date);
            responseText = FILL_GENDER_TEXT;
            createGenderMenu();
            sendMenu(update.getMessage().getChatId(), FILL_GENDER_TEXT, keyboardM1);
        } catch (DateTimeParseException e) {
            sendMessage(update.getMessage().getChatId(), ERROR_BIRTH_DATE_TEXT);
        }
    }

    /**
     *  Заполнение пола с последующей отправкой в базу данных
     */
    private void fillGender(Update update) {
        user1.setGender(update.getCallbackQuery().getData());
        responseText = FILL_PHOTO_TEXT;
        sendMessage(update.getCallbackQuery().getMessage().getChatId(), responseText);
    }

    /**
     *  Заполнение фото с последующей отправкой в базу данных
     */
    private void fillPhoto(Update update) {
        List<PhotoSize> photos = update.getMessage().getPhoto();
        PhotoSize photo = photos.get(photos.size()-1);
        String filepath = photo.getFileId();
        user1.setPhotoPath(getPhotoUrl(filepath));
        System.out.println(user1.toString());
        responseText = OUTPUT_FILE_TEXT;
        sendMessage(update.getMessage().getChatId(), responseText);
        addUser(user1);
        WordGenerator wordGenerator = new WordGenerator();
        java.io.File file = wordGenerator.createDocument(user1);
        sendDocument(update.getMessage().getChatId(), file);

    }

    /**
     *  Создание меню для согласия на обработку персональных данных
     */
    private void createPersonalDataMenu() {
        button1 = InlineKeyboardButton.builder()
                .text(textButton1).callbackData("url")
                .url("https://snptech.ru/")
                .build();
        button2 = InlineKeyboardButton.builder()
                .text(textButton2).callbackData("done")
                .build();
        keyboardM1 = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(button1))
                .keyboardRow(List.of(button2))
                .build();
    }

    /**
     *  Создание меню для выбора пола
     */
    private void createGenderMenu() {
        button1 = InlineKeyboardButton.builder()
                .text("Мужской").callbackData("Мужской")
                .build();
        button2 = InlineKeyboardButton.builder()
                .text("Женский").callbackData("Женский")
                .build();
        keyboardM1 = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(button1))
                .keyboardRow(List.of(button2))
                .build();
    }

    /**
     * Запрашивает ссылку на фото
     * @param fileId id фото
     * @return ссылка на фото
     */
    private String getPhotoUrl(String fileId) {
        try {
            GetFile getFile = new GetFile();
            getFile.setFileId(fileId);
            File file = execute(getFile);

            if (file.getFilePath() != null) {
                String fileUrl = "https://api.telegram.org/file/bot" + getBotToken() + "/" + file.getFilePath();
                System.out.println("Ссылка на фото: " + fileUrl);
                return fileUrl;
            } else {
                System.out.println("Ошибка: ссылки не существует");
                return null;
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Добавляет пользователя в базу данных
     * @param user
     */
    private void addUser(User user) {
        service.addUser(user);
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
