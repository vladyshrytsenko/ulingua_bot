package bot.telegram.umelon.ulingua.service;

import bot.telegram.umelon.ulingua.model.enums.UserWordProgress;

public interface UserWordService {

    void addWordForUser(long userId, long wordId, UserWordProgress progress);
    
    boolean isDailyLimitExceeded(long userId, long userLimit);
}
