package bot.telegram.umelon.ulingua.handler.callback;

import bot.telegram.umelon.ulingua.handler.CallbackHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@RequiredArgsConstructor
public class LocalizationCallbackHandler implements CallbackHandler {

    private final UserService userService;
    private final TelegramUtils telegramUtils;

    @Override
    public void handle(CallbackQuery callbackQuery, LocalMessages localMessages) {

        String callbackData = callbackQuery.getData();
        Long callbackChatId = callbackQuery.getMessage().getChatId();
        Integer callbackMessageId = callbackQuery.getMessage().getMessageId();

        if (callbackData.endsWith(CallbackCommandEnum.CHANGE_BOT_LANG.getValue())) {
            String selectedLang = callbackData.replace(CallbackCommandEnum.CHANGE_BOT_LANG.getValue(), "");
            userService.setBotLanguage(callbackChatId, selectedLang);

            telegramUtils.sendEditMessageText(
                callbackChatId, callbackMessageId, localMessages.get("message.bot_language_changed")
            );
        }
    }
}
