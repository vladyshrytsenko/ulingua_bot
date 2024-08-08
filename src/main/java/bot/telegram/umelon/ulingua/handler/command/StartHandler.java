package bot.telegram.umelon.ulingua.handler.command;

import bot.telegram.umelon.ulingua.handler.CommandHandler;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class StartHandler implements CommandHandler {

    private final TelegramUtils telegramUtils;

    @Override
    public void handle(long chatId, String messageText, Update update) {

        telegramUtils.sendMessage(chatId, "start command");
    }
}
