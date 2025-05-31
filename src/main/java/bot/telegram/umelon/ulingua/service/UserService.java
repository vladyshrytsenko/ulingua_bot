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

    void addUserLanguage(long chatId, long languageId);

    void removeUserLanguage(long chatId, long languageId);

    void setUserCurrentLanguage(long chatId, String langCode);

    void setBotLanguage(long chatId, String langCode);

    void setDailyLimit(long chatId, byte dailyLimit);
}
