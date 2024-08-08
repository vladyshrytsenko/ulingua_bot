package bot.telegram.umelon.ulingua.utils;

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
import java.util.List;

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

    public void sendEditMessageTextWithInlineKeyboard(long chatId, long messageId, String text) {
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

        UserDto byChatId = userService.getByChatId(chatId);
        String nativeLang = byChatId.getNativeLang();

        languages.removeIf(languageDto -> languageDto.getCountryCode().equals(nativeLang));
        for (LanguageDto lang : languages) {
            if (count % buttonsPerRow == 0 && !row.isEmpty()) {
                keyboard.add(row);
                row = new ArrayList<>();
            }
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(lang.getUnicode());

            String callbackData = lang.getCountryCode().concat(CallbackCommandEnum.ADD_LANG.getValue());
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


    public void sendLanguagesInlineKeyboard(Long chatId, String text, String command) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<LanguageDto> languages = languageService.findAll();

        int buttonsPerRow = 3;
        List<InlineKeyboardButton> row = new ArrayList<>();

        int count = 0;
        for (LanguageDto lang : languages) {
            if (count % buttonsPerRow == 0 && !row.isEmpty()) {
                keyboard.add(row);
                row = new ArrayList<>();
            }
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(lang.getUnicode());

            String callbackData = lang.getCountryCode().concat(command);
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

    private static boolean containsEmoji(String text) {
        return containsAny(text, "\uD83C\uDDE6-\uD83C\uDDFA");
    }
}
