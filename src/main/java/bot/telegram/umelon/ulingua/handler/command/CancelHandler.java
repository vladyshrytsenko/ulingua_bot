package bot.telegram.umelon.ulingua.handler.command;

import bot.telegram.umelon.ulingua.handler.CommandHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class CancelHandler implements CommandHandler {

    @Override
    public void handle(long userId, String messageText, Update update, LocalMessages localMessages) {
        userService.setUserState(userId, null);

        telegramUtil.sendDeleteMessageRequest(userId, update.getMessage().getMessageId());
        telegramUtil.sendMessage(userId, localMessages.get("message.command_canceled"), false);
    }

    private final TelegramUtil telegramUtil;
    private final UserService userService;
}
