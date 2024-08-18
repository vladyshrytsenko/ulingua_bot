package bot.telegram.umelon.ulingua.handler;

import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.UserDto;

public interface StateHandler {

    void handle(long chatId, String messageText, UserDto currentUser, LocalMessages localMessages);
}
