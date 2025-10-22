package bot.telegram.umelon.ulingua.util;

import bot.telegram.umelon.ulingua.model.ButtonData;
import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum;
import bot.telegram.umelon.ulingua.service.LanguageService;
import bot.telegram.umelon.ulingua.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.Math.*;

@Component
@RequiredArgsConstructor
public class TelegramUtil {

    public void sendMessage(long userId, String text, boolean isCancellable) {
        SendMessage sendMessage = SendMessage.builder()
            .chatId(userId)
            .text(text)
            .build();

        if (isCancellable) {
            ReplyKeyboardMarkup replyKeyboardMarkup = ReplyKeyboardMarkup.builder()
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .build();

            List<KeyboardRow> keyboardRows = new ArrayList<>();
            KeyboardRow row = new KeyboardRow();
            row.add("❌ Cancel");
            keyboardRows.add(row);

            replyKeyboardMarkup.setKeyboard(keyboardRows);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }

        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void sendMessageWithInlineKeyboard(long userId, String text, List<String> keyboardTextList, CallbackCommandEnum command) {
        SendMessage sendMessage = SendMessage.builder()
            .chatId(userId)
            .text(text)
            .build();

        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();

        for (String keyboardText : keyboardTextList) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text(keyboardText)
                .callbackData(keyboardText.concat(command.getValue()))
                .build();

            InlineKeyboardRow row = new InlineKeyboardRow();
            row.add(button);
            keyboardRows.add(row);
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
            .keyboard(keyboardRows)
            .build();

        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public void sendEditMessageText(long userId, long messageId, String text){
        EditMessageText message = EditMessageText.builder()
            .chatId(userId)
            .text(text)
            .messageId(toIntExact(messageId))
            .build();

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.getMessage();
        }
    }

    public void sendDeleteMessageRequest(long userId, long messageId) {
        DeleteMessage deleteMessage = DeleteMessage.builder()
            .chatId(userId)
            .messageId(toIntExact(messageId))
            .build();

        try {
            telegramClient.execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendEditMessageTextWithInlineKeyboard(long userId, long messageId, String text, CallbackCommandEnum command) {
        EditMessageText message = EditMessageText.builder()
            .chatId(userId)
            .text(text)
            .messageId(Math.toIntExact(messageId))
            .build();

        List<InlineKeyboardRow> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        int buttonsPerRow = 3;

        List<LanguageDto> languages = languageService.findAll();
        UserDto currentUser = userService.getById(userId);

        if (currentUser != null) {
            languages.removeIf(
                languageDto -> currentUser.languages().stream()
                                   .anyMatch(lang -> lang.countryCode().equals(languageDto.countryCode())) ||
                               currentUser.nativeLang().equals(languageDto.countryCode())
            );
        }

        for (int i = 0; i < languages.size(); i++) {
            LanguageDto lang = languages.get(i);

            InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text(lang.unicode())
                .callbackData(lang.countryCode().concat(command.getValue()))
                .build();

            row.add(button);

            if ((i + 1) % buttonsPerRow == 0 || i == languages.size() - 1) {
                keyboard.add(new InlineKeyboardRow(new ArrayList<>(row)));
                row.clear();
            }
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
            .keyboard(keyboard)
            .build();

        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendEditMessageWithInlineKeyboard(long userId, long messageId, String text, List<String> keyboardTextList, CallbackCommandEnum command) {
        EditMessageText editMessage = EditMessageText.builder()
            .chatId(userId)
            .messageId(Math.toIntExact(messageId))
            .text(text)
            .build();

        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();

        for (String keyboardText : keyboardTextList) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text(keyboardText)
                .callbackData(keyboardText.concat(command.getValue()))
                .build();

            InlineKeyboardRow row = new InlineKeyboardRow();
            row.add(button);
            keyboardRows.add(row);
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
            .keyboard(keyboardRows)
            .build();

        editMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            telegramClient.execute(editMessage);
        } catch (TelegramApiException e) {
            // Якщо не вдається відредагувати — відправляємо нове повідомлення
            this.sendMessageWithInlineKeyboard(userId, text, keyboardTextList, command);
        }
    }

    public void sendLanguagesInlineKeyboard(Long userId, String text, CallbackCommandEnum command) {
        SendMessage message = SendMessage.builder()
            .chatId(userId)
            .text(text)
            .build();

        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();
        List<LanguageDto> languages = languageService.findAll();

        int buttonsPerRow = 3;

        UserDto currentUser = userService.getById(userId);
        if (currentUser != null) {
            languages.removeIf(
                languageDto -> currentUser.languages().stream()
                                   .anyMatch(lang -> lang.countryCode().equals(languageDto.countryCode())) ||
                               currentUser.nativeLang().equals(languageDto.countryCode())
            );
        }

        InlineKeyboardRow currentRow = new InlineKeyboardRow();
        for (int i = 0; i < languages.size(); i++) {
            LanguageDto lang = languages.get(i);

            String callbackData = lang.countryCode().concat(command.getValue());

            InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text(lang.unicode())
                .callbackData(callbackData)
                .build();

            currentRow.add(button);

            if ((i + 1) % buttonsPerRow == 0 || i == languages.size() - 1) {
                keyboardRows.add(currentRow);
                currentRow = new InlineKeyboardRow();
            }
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
            .keyboard(keyboardRows)
            .build();

        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendUserLanguagesInlineKeyboard(Long userId, String text, CallbackCommandEnum command) {
        SendMessage message = SendMessage.builder()
            .chatId(userId)
            .text(text)
            .build();

        InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder().build();
        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();

        int buttonsPerRow = 3;
        List<InlineKeyboardButton> row = new ArrayList<>();

        UserDto currentUser = userService.getById(userId);
        Set<LanguageDto> languages;

        if (currentUser == null) {
            languages = new HashSet<>(languageService.findAll());
        } else {
            languages = currentUser.languages();
            if (command.equals(CallbackCommandEnum.CHANGE_BOT_LANG)) {
                LanguageDto byCountryCode = languageService.getByCountryCode(currentUser.nativeLang());
                languages.add(byCountryCode);
            }
        }

        int count = 0;
        for (LanguageDto lang : languages) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text(lang.unicode())
                .callbackData(lang.countryCode().concat(command.getValue()))
                .build();

            row.add(button);
            count++;

            if (count % buttonsPerRow == 0) {
                keyboardRows.add(new InlineKeyboardRow(row));
                row = new ArrayList<>();
            }
        }

        if (!row.isEmpty()) {
            keyboardRows.add(new InlineKeyboardRow(row));
        }

        inlineKeyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendEditMessageTextWithInlineKeyboard(Long userId, long messageId, String text, List<ButtonData> buttonDataList) {
        EditMessageText message = EditMessageText.builder()
            .chatId(userId)
            .text(text)
            .messageId(Math.toIntExact(messageId))
            .build();

        Map<Integer, List<InlineKeyboardButton>> rowsMap = new HashMap<>();

        for (ButtonData buttonData : buttonDataList) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text(buttonData.getText())
                .callbackData(buttonData.getCallbackCommand().getValue())
                .build();

            rowsMap.computeIfAbsent(buttonData.getRowNumber(), k -> new ArrayList<>()).add(button);
        }

        List<InlineKeyboardRow> rowsInLine = new ArrayList<>();
        for (List<InlineKeyboardButton> row : rowsMap.values()) {
            rowsInLine.add(new InlineKeyboardRow(row));
        }

        InlineKeyboardMarkup markupInLine = InlineKeyboardMarkup.builder()
            .keyboard(rowsInLine)
            .build();

        message.setReplyMarkup(markupInLine);

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            this.sendMessageTextWithInlineKeyboard(userId, text, buttonDataList);
        }
    }

    public void sendMessageTextWithInlineKeyboard(Long userId, String text, List<ButtonData> buttonDataList) {
        SendMessage message = SendMessage.builder()
            .chatId(userId)
            .text(text)
            .build();

        Map<Integer, List<InlineKeyboardButton>> rowsMap = new HashMap<>();

        for (ButtonData buttonData : buttonDataList) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text(buttonData.getText())
                .callbackData(buttonData.getCallbackCommand().getValue())
                .build();

            rowsMap.computeIfAbsent(buttonData.getRowNumber(), k -> new ArrayList<>()).add(button);
        }

        List<InlineKeyboardRow> rowsInLine = new ArrayList<>();
        for (List<InlineKeyboardButton> row : rowsMap.values()) {
            rowsInLine.add(new InlineKeyboardRow(row));
        }

        InlineKeyboardMarkup markupInLine = InlineKeyboardMarkup.builder()
            .keyboard(rowsInLine)
            .build();

        message.setReplyMarkup(markupInLine);

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendInlineKeyboard(Long userId, String text, List<ButtonData> buttonDataList) {
        SendMessage message = SendMessage.builder()
            .chatId(userId)
            .text(text)
            .build();

        Map<Integer, List<InlineKeyboardButton>> rowsMap = new HashMap<>();

        for (ButtonData buttonData : buttonDataList) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text(buttonData.getText())
                .callbackData(buttonData.getCallbackCommand().getValue())
                .build();

            rowsMap.computeIfAbsent(buttonData.getRowNumber(), k -> new ArrayList<>()).add(button);
        }

        List<InlineKeyboardRow> rowsInLine = new ArrayList<>();
        for (List<InlineKeyboardButton> row : rowsMap.values()) {
            rowsInLine.add(new InlineKeyboardRow(row));
        }

        InlineKeyboardMarkup markupInLine = InlineKeyboardMarkup.builder()
            .keyboard(rowsInLine)
            .build();

        message.setReplyMarkup(markupInLine);

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Value("${bot.api-key}")
    private String token;

    private TelegramClient telegramClient;

    @PostConstruct
    public void init() {
        this.telegramClient = new OkHttpTelegramClient(this.token);
    }

    private final LanguageService languageService;
    private final UserService userService;
}
