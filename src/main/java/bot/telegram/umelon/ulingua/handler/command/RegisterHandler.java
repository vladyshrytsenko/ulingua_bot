package bot.telegram.umelon.ulingua.handler.command;

import bot.telegram.umelon.ulingua.handler.CommandHandler;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.utils.CountryFlagUtil;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RegisterHandler implements CommandHandler {

    private final UserService userService;
    private final CountryFlagUtil countryFlagUtil;
    private final TelegramUtils telegramUtils;

    @Override
    public void handle(long chatId, String messageText, Update update) {

        UserDto currentUserDto = userService.getByChatId(chatId);
        if (currentUserDto == null) {
            String text = "Виберіть вашу рідну мову:";
            telegramUtils.sendLanguagesInlineKeyboard(chatId, text, CallbackCommandEnum.ADD_NATIVE_LANG.getValue());
        } else {
            List<String> list = new ArrayList<>();
            currentUserDto.getLanguages().forEach(languageDto -> {
                String langFlag = countryFlagUtil.getFlagByCountry(languageDto.getCountryCode());
                list.add(langFlag);
            });
            telegramUtils.sendMessage(chatId, "Ви вже зареєстровані в боті. Список мов для вивчення:\n" + list);
        }
    }
}
