package bot.telegram.umelon.ulingua.handler;

import bot.telegram.umelon.ulingua.model.dto.UserDto;

public interface StateHandler {

    void handle(long chatId, String messageText, UserDto currentUserDto);
}
