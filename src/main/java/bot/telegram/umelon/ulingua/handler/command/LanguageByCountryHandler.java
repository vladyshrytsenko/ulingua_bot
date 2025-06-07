package bot.telegram.umelon.ulingua.handler.command;

import bot.telegram.umelon.ulingua.handler.CommandHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.enums.UserState;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class LanguageByCountryHandler implements CommandHandler {

    @Override
    public void handle(long userId, String messageText, Update update, LocalMessages localMessages) {
        userService.setUserState(userId, null);

        String message;
        UserDto currentUserDto = this.userService.getById(update.getMessage().getChat().getId());

        if (currentUserDto == null) {
            message = localMessages.get("message.register_required");
            telegramUtils.sendMessage(userId, message);
        } else {
            message = localMessages.get("message.select_language_by_country");
            userService.setUserState(userId, UserState.AWAITING_COUNTRY);

            telegramUtils.sendDeleteMessageRequest(userId, update.getMessage().getMessageId());
            telegramUtils.sendMessage(userId, message);
        }
    }

    private final UserService userService;
    private final TelegramUtils telegramUtils;
}