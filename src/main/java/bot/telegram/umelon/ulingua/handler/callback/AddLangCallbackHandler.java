package bot.telegram.umelon.ulingua.handler.callback;

import bot.telegram.umelon.ulingua.handler.CallbackHandler;
import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.entity.User;
import bot.telegram.umelon.ulingua.service.LanguageService;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.utils.CountryFlagUtil;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@RequiredArgsConstructor
public class AddLangCallbackHandler implements CallbackHandler {

    private final CountryFlagUtil countryFlagUtil;
    private final LanguageService languageService;
    private final UserService userService;
    private final TelegramUtils telegramUtils;

    @Override
    public void handle(CallbackQuery callbackQuery) {

        String callbackData = callbackQuery.getData();
        Long callbackChatId = callbackQuery.getMessage().getChatId();
        Integer callbackMessageId = callbackQuery.getMessage().getMessageId();

        String selectedLanguageFlag = countryFlagUtil.getFlagByCountry(callbackData.substring(0, 2));

        LanguageDto foundLanguage = languageService.getByCountryCode(callbackData.substring(0, 2));
        if (foundLanguage != null) {

            org.telegram.telegrambots.meta.api.objects.User from = callbackQuery.getFrom();
            User user = User.builder()
                .chatId(callbackChatId)
                .firstName(from.getFirstName())
                .lastName(from.getLastName())
                .username(from.getUserName())
                .build();

            userService.save(user);
            userService.addLanguageToUser(callbackChatId, foundLanguage.getId());
            userService.setCurrentLanguageForUser(callbackChatId, foundLanguage.getCountryCode());

            String text = String.format("Ви обрали мову для вивчення: %s.", selectedLanguageFlag);
            telegramUtils.sendEditMessageText(callbackChatId, callbackMessageId, text);
        }
    }
}
