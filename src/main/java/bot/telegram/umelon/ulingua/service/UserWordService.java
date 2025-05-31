package bot.telegram.umelon.ulingua.service;

import bot.telegram.umelon.ulingua.model.entity.User;
import bot.telegram.umelon.ulingua.model.entity.UserWord;
import bot.telegram.umelon.ulingua.model.enums.UserWordProgress;

public interface UserWordService {

//    UserWord getByUserId(long id);
//    UserWord save(User user);
    void addWordForUser(long chatId, long wordId, UserWordProgress progress);
    void removeWord(long chatId, long wordId);
    boolean isDailyLimitExceeded(long userId, long userLimit);
}
