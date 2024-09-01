package bot.telegram.umelon.ulingua.handler.command;

import bot.telegram.umelon.ulingua.handler.CommandHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.enums.CountryCodeEnum;
import bot.telegram.umelon.ulingua.service.LanguageService;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.utils.LocaleUtils;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;
import java.util.Objects;

import static java.lang.String.*;

@Component
@RequiredArgsConstructor
public class AlphabetHandler implements CommandHandler {

    private final UserService userService;
    private final LanguageService languageService;
    private final TelegramUtils telegramUtils;

    @Override
    public void handle(long chatId, String messageText, Update update, LocalMessages localMessages) {
        userService.setUserState(chatId, null);

        UserDto currentUserDto = userService.getByChatId(update.getMessage().getChatId());
        if (currentUserDto == null) {
            String message = localMessages.get("message.register_required");
            telegramUtils.sendMessage(chatId, message);
        } else {
            if (Objects.equals(currentUserDto.getCurrentLang(), CountryCodeEnum.CN.name())) {
                telegramUtils.sendMessage(chatId, "The Chinese language doesn't have an alphabet, but uses a hieroglyphic system instead.");
            } else {

                LanguageDto byCountryCode = languageService.getByCountryCode(currentUserDto.getCurrentLang());
                Locale locale = LocaleUtils.getLocale(currentUserDto.getCurrentLang());
                String alphabet = languageService.getAlphabet(locale);

                telegramUtils.sendMessage(chatId, format(localMessages.get("message.alphabet_info"), byCountryCode.getUnicode(), alphabet));
            }
        }
    }
}
