package bot.telegram.umelon.ulingua.utils;

import bot.telegram.umelon.ulingua.model.ButtonData;
import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum;
import bot.telegram.umelon.ulingua.service.LanguageService;
import bot.telegram.umelon.ulingua.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.containsAny;

@Component
@RequiredArgsConstructor
public class TelegramUtils {

    private final TelegramLongPollingBot bot;
    private final LanguageService languageService;
    private final UserService userService;

    public void sendMessage(long chatId, String text) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeKeyBoard(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Removing keyboard...");

        ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
        replyKeyboardRemove.setRemoveKeyboard(true);
        message.setReplyMarkup(replyKeyboardRemove);

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendEditMessageText(long chatId, long messageId, String text){
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.getMessage();
        }
    }

    public void sendEditMessageTextWithInlineKeyboard(long chatId, long messageId, String text, CallbackCommandEnum command) {
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<LanguageDto> languages = languageService.findAll();
        int buttonsPerRow = 3;
        List<InlineKeyboardButton> row = new ArrayList<>();

        int count = 0;

        UserDto currentUser = userService.getByChatId(chatId);

        if (currentUser != null) {
            languages.removeIf(
                languageDto -> currentUser.getLanguages().stream()
                                   .anyMatch(lang -> lang.getCountryCode().equals(languageDto.getCountryCode())) ||
                               currentUser.getNativeLang().equals(languageDto.getCountryCode())
            );
        }

        for (LanguageDto lang : languages) {
            if (count % buttonsPerRow == 0 && !row.isEmpty()) {
                keyboard.add(row);
                row = new ArrayList<>();
            }
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(lang.getUnicode());

            String callbackData = lang.getCountryCode().concat(command.getValue());
            button.setCallbackData(callbackData);
            row.add(button);
            count++;
        }
        if (!row.isEmpty()) {
            keyboard.add(row);
        }

        inlineKeyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public void sendLanguagesInlineKeyboard(Long chatId, String text, CallbackCommandEnum command) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<LanguageDto> languages = languageService.findAll();

        int buttonsPerRow = 3;
        List<InlineKeyboardButton> row = new ArrayList<>();

        UserDto currentUser = userService.getByChatId(chatId);
        if (currentUser != null) {
            languages.removeIf(
                languageDto -> currentUser.getLanguages().stream()
                                   .anyMatch(lang -> lang.getCountryCode().equals(languageDto.getCountryCode())) ||
                               currentUser.getNativeLang().equals(languageDto.getCountryCode())
            );
        }

        int count = 0;
        for (LanguageDto lang : languages) {
            if (count % buttonsPerRow == 0 && !row.isEmpty()) {
                keyboard.add(row);
                row = new ArrayList<>();
            }
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(lang.getUnicode());

            String callbackData = lang.getCountryCode().concat(command.getValue());
            button.setCallbackData(callbackData);
            row.add(button);
            count++;
        }
        if (!row.isEmpty()) {
            keyboard.add(row);
        }

        inlineKeyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendUserLanguagesInlineKeyboard(Long chatId, String text, CallbackCommandEnum command) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        int buttonsPerRow = 3;
        List<InlineKeyboardButton> row = new ArrayList<>();

        UserDto currentUser = userService.getByChatId(chatId);
        Set<LanguageDto> userLanguages = currentUser.getLanguages();
        if (command.equals(CallbackCommandEnum.CHANGE_BOT_LANG)) {
            LanguageDto byCountryCode = languageService.getByCountryCode(currentUser.getNativeLang());
            userLanguages.add(byCountryCode);
        }

        int count = 0;
        for (LanguageDto lang : userLanguages) {
            if (count % buttonsPerRow == 0 && !row.isEmpty()) {
                keyboard.add(new ArrayList<>(row));
                row.clear();
            }
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(lang.getUnicode());
            button.setCallbackData(lang.getCountryCode().concat(command.getValue()));
            row.add(button);
            count++;
        }
        if (!row.isEmpty()) {
            keyboard.add(row);
        }

        inlineKeyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendInlineKeyboard(Long chatId, String text, List<ButtonData> buttonDataList) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        Map<Integer, List<InlineKeyboardButton>> rowsMap = new HashMap<>();

        for (ButtonData buttonData : buttonDataList) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(buttonData.getText());
            button.setCallbackData(buttonData.getCallbackCommand().getValue());
            rowsMap.computeIfAbsent(buttonData.getRowNumber(), k -> new ArrayList<>()).add(button);
        }

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>(rowsMap.values());

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private static boolean containsEmoji(String text) {
        return containsAny(text, "\uD83C\uDDE6-\uD83C\uDDFA");
    }
}
