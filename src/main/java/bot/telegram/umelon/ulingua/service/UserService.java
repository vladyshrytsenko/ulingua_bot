package bot.telegram.umelon.ulingua.service;

import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.enums.UserState;

public interface UserService {

    void setUserState(long userId, UserState state);
    UserState getUserState(long userId);
    UserDto getById(long id);
    UserDto save(UserDto requestDto);
    void addUserLanguage(long userId, LanguageDto languageDto);
    void removeUserLanguage(long userId, long languageId);
    void setUserCurrentLanguage(long userId, String langCode);
    void setDailyLimit(long userId, byte dailyLimit);
}
