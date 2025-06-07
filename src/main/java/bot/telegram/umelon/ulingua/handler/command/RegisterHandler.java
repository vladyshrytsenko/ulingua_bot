package bot.telegram.umelon.ulingua.handler.command;

import bot.telegram.umelon.ulingua.handler.CommandHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
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

    @Override
    public void handle(long userId, String messageText, Update update, LocalMessages localMessages) {

        UserDto currentUserDto = userService.getById(userId);
        if (currentUserDto == null) {
            String text = localMessages.get("message.select_native_language");
            telegramUtils.sendLanguagesInlineKeyboard(userId, text, CallbackCommandEnum.ADD_NATIVE_LANG);
        } else {
            List<String> list = new ArrayList<>();
            currentUserDto.getLanguages().forEach(languageDto -> {
                String langFlag = countryFlagUtil.getFlagByCountry(languageDto.getCountryCode());
                list.add(langFlag);
            });

            telegramUtils.sendDeleteMessageRequest(userId, update.getMessage().getMessageId());
            telegramUtils.sendMessage(userId, localMessages.get("message.already_registered") + list);
        }
    }

    private final UserService userService;
    private final CountryFlagUtil countryFlagUtil;
    private final TelegramUtils telegramUtils;
}
