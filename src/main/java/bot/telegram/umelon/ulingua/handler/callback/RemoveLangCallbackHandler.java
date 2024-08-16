package bot.telegram.umelon.ulingua.handler.callback;

import bot.telegram.umelon.ulingua.handler.CallbackHandler;
import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum;
import bot.telegram.umelon.ulingua.service.LanguageService;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.utils.CountryFlagUtil;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@RequiredArgsConstructor
public class RemoveLangCallbackHandler implements CallbackHandler {

    private final CountryFlagUtil countryFlagUtil;
    private final LanguageService languageService;
    private final UserService userService;
    private final TelegramUtils telegramUtils;

    @Override
    public void handle(CallbackQuery callbackQuery) {

        String callbackData = callbackQuery.getData();
        Long callbackChatId = callbackQuery.getMessage().getChatId();
        Integer callbackMessageId = callbackQuery.getMessage().getMessageId();

        if (callbackData.equals(CallbackCommandEnum.REMOVE_LANG.getValue())) {
            telegramUtils.sendUserLanguagesInlineKeyboard(callbackChatId, "Виберiть мову для видалення:", CallbackCommandEnum.REMOVE_LANG);

        } else if (callbackData.endsWith(CallbackCommandEnum.REMOVE_LANG.getValue())) {
            String selectedLanguageFlag = countryFlagUtil.getFlagByCountry(callbackData.substring(0, 2));

            LanguageDto foundLanguage = languageService.getByCountryCode(callbackData.substring(0, 2));
            if (foundLanguage != null) {
                userService.removeUserLanguage(callbackChatId, foundLanguage.getId());

                String text = String.format("Ви видалили мову зі списку: %s.", selectedLanguageFlag);
                telegramUtils.sendEditMessageText(callbackChatId, callbackMessageId, text);
            }
        }
    }
}
