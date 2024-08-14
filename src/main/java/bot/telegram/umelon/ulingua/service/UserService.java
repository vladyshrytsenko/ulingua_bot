package bot.telegram.umelon.ulingua.service;

import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.entity.User;
import bot.telegram.umelon.ulingua.model.enums.UserState;

public interface UserService {

    void setUserState(long chatId, UserState state);

    UserState getUserState(long chatId);

    UserDto getById(long id);

    UserDto getByChatId(long id);

    UserDto save(User user);

    UserDto addLanguageToUser(long chatId, long languageId);

    UserDto setCurrentLanguageForUser(long chatId, String langCode);

    void addWordForUser(long userId, long wordId);
}
