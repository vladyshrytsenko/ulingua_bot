package bot.telegram.umelon.ulingua.handler.command;

import bot.telegram.umelon.ulingua.handler.CommandHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.enums.UserState;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class LanguageByCountryHandler implements CommandHandler {

    @Override
    public void handle(long userId, String messageText, Update update, LocalMessages localMessages) {
        userService.setUserState(userId, null);

        String message = localMessages.get("message.select_language_by_country");
        userService.setUserState(userId, UserState.AWAITING_COUNTRY);

        telegramUtil.sendDeleteMessageRequest(userId, update.getMessage().getMessageId());
        telegramUtil.sendMessage(userId, message, true);
    }

    private final UserService userService;
    private final TelegramUtil telegramUtil;
}