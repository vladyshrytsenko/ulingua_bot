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
public class SetCurrentLangCallbackHandler implements CallbackHandler {

    private final UserService userService;
    private final TelegramUtils telegramUtils;

    @Override
    public void handle(CallbackQuery callbackQuery, LocalMessages localMessages) {

        String callbackData = callbackQuery.getData();
        Long callbackChatId = callbackQuery.getMessage().getChatId();
        Integer callbackMessageId = callbackQuery.getMessage().getMessageId();

        if (callbackData.equals(CallbackCommandEnum.SET_CURRENT_LANG.getValue())) {
            telegramUtils.sendUserLanguagesInlineKeyboard(
                callbackChatId, localMessages.get("message.select_new_current_language"), CallbackCommandEnum.SET_CURRENT_LANG
            );

        } else if (callbackData.endsWith(CallbackCommandEnum.SET_CURRENT_LANG.getValue())) {
            String selectedLang = callbackData.replace(CallbackCommandEnum.SET_CURRENT_LANG.getValue(), "");
            userService.setUserCurrentLanguage(callbackChatId, selectedLang);

            telegramUtils.sendEditMessageText(
                callbackChatId, callbackMessageId, localMessages.get("message.current_language_changed")
            );
        }
    }
}
