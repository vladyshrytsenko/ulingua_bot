package bot.telegram.umelon.ulingua.handler.command;

import bot.telegram.umelon.ulingua.handler.CommandHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class LocalizationHandler implements CommandHandler {

    @Override
    public void handle(long userId, String messageText, Update update, LocalMessages localMessages) {
        UserDto currentUserDto = userService.getById(update.getMessage().getChatId());
        if (currentUserDto == null) {
            String message = localMessages.get("message.register_required");
            telegramUtils.sendMessage(userId, message);
        } else {
            telegramUtils.sendDeleteMessageRequest(userId, update.getMessage().getMessageId());
            telegramUtils.sendUserLanguagesInlineKeyboard(
                userId, localMessages.get("message.select_bot_language"), CallbackCommandEnum.CHANGE_BOT_LANG
            );
        }
    }

    private final TelegramUtils telegramUtils;
    private final UserService userService;
}
