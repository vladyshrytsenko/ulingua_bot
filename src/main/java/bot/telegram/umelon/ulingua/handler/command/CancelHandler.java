package bot.telegram.umelon.ulingua.handler.command;

import bot.telegram.umelon.ulingua.handler.CommandHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class CancelHandler implements CommandHandler {

    private final TelegramUtils telegramUtils;
    private final UserService userService;

    @Override
    public void handle(long chatId, String messageText, Update update, LocalMessages localMessages) {

        userService.setUserState(chatId, null);
        telegramUtils.sendMessage(chatId, localMessages.get("message.command_canceled"));
    }
}
