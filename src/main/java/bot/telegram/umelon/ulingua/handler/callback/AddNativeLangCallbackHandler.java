package bot.telegram.umelon.ulingua.handler.callback;

import bot.telegram.umelon.ulingua.handler.CallbackHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum;
import bot.telegram.umelon.ulingua.service.LanguageService;
import bot.telegram.umelon.ulingua.service.LocalizationService;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.util.CountryFlagUtil;
import bot.telegram.umelon.ulingua.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@RequiredArgsConstructor
public class AddNativeLangCallbackHandler implements CallbackHandler {

    @Override
    public void handle(CallbackQuery callbackQuery, LocalMessages localMessages) {
        String callbackData = callbackQuery.getData();
        Long callbackChatId = callbackQuery.getMessage().getChatId();
        Integer callbackMessageId = callbackQuery.getMessage().getMessageId();

        String selectedNativeLanguageFlag = countryFlagUtil.getFlagByCountry(callbackData.substring(0, 2));
        LanguageDto foundLanguage = languageService.getByCountryCode(callbackData.substring(0, 2));

        if (foundLanguage != null) {
            org.telegram.telegrambots.meta.api.objects.User from = callbackQuery.getFrom();
            UserDto userRequest = UserDto.builder()
                .id(callbackChatId)
                .firstname(from.getFirstName())
                .lastname(from.getLastName())
                .username(from.getUserName())
                .nativeLang(foundLanguage.countryCode())
                .dailyLimit((byte) 5)
                .build();

            userService.save(userRequest);
            localizationService.setBotLanguage(callbackChatId, foundLanguage.countryCode());

            telegramUtil.sendEditMessageTextWithInlineKeyboard(
                callbackChatId,
                callbackMessageId,
                localMessages.get("message.native_language_selected").formatted(selectedNativeLanguageFlag),
                CallbackCommandEnum.ADD_LANG
            );
        }
    }

    private final CountryFlagUtil countryFlagUtil;
    private final LanguageService languageService;
    private final UserService userService;
    private final LocalizationService localizationService;
    private final TelegramUtil telegramUtil;
}
