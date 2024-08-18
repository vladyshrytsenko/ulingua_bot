package bot.telegram.umelon.ulingua.handler;

import bot.telegram.umelon.ulingua.model.LocalMessages;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandHandler {

    void handle(long chatId, String messageText, Update update, LocalMessages localMessages);
}
