package bot.telegram.umelon.ulingua.handler;

import bot.telegram.umelon.ulingua.model.LocalMessages;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackHandler {

    void handle(CallbackQuery callbackQuery, LocalMessages localMessages);
}
