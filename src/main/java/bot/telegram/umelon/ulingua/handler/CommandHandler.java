package bot.telegram.umelon.ulingua.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandHandler {

    void handle(long chatId, String messageText, Update update);
}
