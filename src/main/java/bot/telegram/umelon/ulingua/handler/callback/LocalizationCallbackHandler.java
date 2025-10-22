package bot.telegram.umelon.ulingua.handler.callback;

import bot.telegram.umelon.ulingua.handler.CallbackHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum;
import bot.telegram.umelon.ulingua.service.LocalizationService;
import bot.telegram.umelon.ulingua.util.LocaleUtil;
import bot.telegram.umelon.ulingua.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class LocalizationCallbackHandler implements CallbackHandler {

    @Override
    public void handle(CallbackQuery callbackQuery, LocalMessages localMessages) {
        String callbackData = callbackQuery.getData();
        Long callbackChatId = callbackQuery.getMessage().getChatId();
        Integer callbackMessageId = callbackQuery.getMessage().getMessageId();

        if (callbackData.endsWith(CallbackCommandEnum.CHANGE_BOT_LANG.getValue())) {
            String selectedLang = callbackData.replace(CallbackCommandEnum.CHANGE_BOT_LANG.getValue(), "");
            localizationService.setBotLanguage(callbackChatId, selectedLang);

            Locale locale = LocaleUtil.getLocale(selectedLang);
            localMessages = new LocalMessages(locale);

            telegramUtil.sendEditMessageText(
                callbackChatId, callbackMessageId, localMessages.get("message.bot_language_changed")
            );
        }
    }

    private final LocalizationService localizationService;
    private final TelegramUtil telegramUtil;
}
