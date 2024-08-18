package bot.telegram.umelon.ulingua.handler.callback;

import bot.telegram.umelon.ulingua.handler.CallbackHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.entity.User;
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
public class AddNativeLangCallbackHandler implements CallbackHandler {

    private final CountryFlagUtil countryFlagUtil;
    private final LanguageService languageService;
    private final UserService userService;
    private final TelegramUtils telegramUtils;

    @Override
    public void handle(CallbackQuery callbackQuery, LocalMessages localMessages) {

        String callbackData = callbackQuery.getData();
        Long callbackChatId = callbackQuery.getMessage().getChatId();
        Integer callbackMessageId = callbackQuery.getMessage().getMessageId();

        String selectedNativeLanguageFlag = countryFlagUtil.getFlagByCountry(callbackData.substring(0, 2));

        LanguageDto foundLanguage = languageService.getByCountryCode(callbackData.substring(0, 2));
        if (foundLanguage != null) {

            org.telegram.telegrambots.meta.api.objects.User from = callbackQuery.getFrom();
            User user = User.builder()
                .chatId(callbackChatId)
                .firstName(from.getFirstName())
                .lastName(from.getLastName())
                .username(from.getUserName())
                .nativeLang(foundLanguage.getCountryCode())
                .localization(foundLanguage.getCountryCode())
                .build();

            String text = String.format(
                localMessages.get("message.native_language_selected"),
                selectedNativeLanguageFlag
            );

            userService.save(user);
            telegramUtils.sendEditMessageTextWithInlineKeyboard(
                callbackChatId, callbackMessageId, text, CallbackCommandEnum.ADD_LANG
            );
        }
    }
}
