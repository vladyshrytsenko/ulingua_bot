package bot.telegram.umelon.ulingua.handler.callback;

import bot.telegram.umelon.ulingua.handler.CallbackHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.dto.LocalizationDto;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum;
import bot.telegram.umelon.ulingua.service.LanguageService;
import bot.telegram.umelon.ulingua.service.LocalizationService;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.util.CountryFlagUtil;
import bot.telegram.umelon.ulingua.util.LocaleUtil;
import bot.telegram.umelon.ulingua.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class AddLangCallbackHandler implements CallbackHandler {

    @Override
    public void handle(CallbackQuery callbackQuery, LocalMessages localMessages) {
        String callbackData = callbackQuery.getData();
        Long callbackChatId = callbackQuery.getMessage().getChatId();
        Integer callbackMessageId = callbackQuery.getMessage().getMessageId();

        if (callbackData.equals(CallbackCommandEnum.ADD_LANG.getValue())) {
            telegramUtil.sendLanguagesInlineKeyboard(
                callbackChatId, localMessages.get("message.select_language_to_add"), CallbackCommandEnum.ADD_LANG
            );

        } else if (callbackData.endsWith(CallbackCommandEnum.ADD_LANG.getValue())) {
            String selectedLanguageFlag = countryFlagUtil.getFlagByCountry(callbackData.substring(0, 2));

            LanguageDto foundLanguage = languageService.getByCountryCode(callbackData.substring(0, 2));
            if (foundLanguage != null) {
                userService.addUserLanguage(callbackChatId, foundLanguage);

                LocalizationDto localizationDto = this.localizationService.getByChatId(callbackChatId);
                String currentLocalization = localizationDto.langCode();

                UserDto currentUser = userService.getById(callbackChatId);
                boolean isLocalizationBelongsToUser = currentUser.languages().stream()
                    .anyMatch(lang -> lang.countryCode().equals(currentLocalization));

                if (!isLocalizationBelongsToUser) {
                    Locale locale = LocaleUtil.getLocale(currentUser.nativeLang());
                    localMessages = new LocalMessages(locale);
                }

                String text = localMessages.get("message.language_selected").formatted(selectedLanguageFlag);
                telegramUtil.sendEditMessageText(callbackChatId, callbackMessageId, text);
            }
        }
    }

    private final CountryFlagUtil countryFlagUtil;
    private final LanguageService languageService;
    private final LocalizationService localizationService;
    private final UserService userService;
    private final TelegramUtil telegramUtil;
}
