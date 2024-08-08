package bot.telegram.umelon.ulingua.handler;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackHandler {

    void handle(CallbackQuery callbackQuery);
}
