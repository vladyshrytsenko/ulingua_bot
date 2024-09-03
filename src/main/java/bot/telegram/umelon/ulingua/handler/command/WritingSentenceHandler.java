package bot.telegram.umelon.ulingua.handler.command;

import bot.telegram.umelon.ulingua.handler.CommandHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.enums.UserState;
import bot.telegram.umelon.ulingua.service.LanguageService;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class WritingSentenceHandler implements CommandHandler {

    private final UserService userService;
    private final LanguageService languageService;
    private final TelegramUtils telegramUtils;

    @Override
    public void handle(long chatId, String messageText, Update update, LocalMessages localMessages) {
        userService.setUserState(chatId, null);

        String message;
        UserDto currentUserDto = this.userService.getByChatId(update.getMessage().getChat().getId());

        if (currentUserDto == null) {
            message = localMessages.get("message.register_required");
            telegramUtils.sendMessage(chatId, message);
        } else {
            LanguageDto languageDto = languageService.getByCountryCode(currentUserDto.getCurrentLang());
            message = String.format(localMessages.get("message.conversation.sentence"), languageDto.getUnicode());
            userService.setUserState(chatId, UserState.AWAITING_SENTENCE);
            telegramUtils.sendMessage(chatId, message);
        }
    }
}
