package bot.telegram.umelon.ulingua.service;

import bot.telegram.umelon.ulingua.model.entity.UserWord;
import bot.telegram.umelon.ulingua.model.enums.UserWordProgress;

import java.util.List;

public interface UserWordService {

    void addWordForUser(long userId, long wordId, UserWordProgress progress);
    List<UserWord> findAll(long userId);
    boolean isDailyLimitExceeded(long userId, long userLimit);
}
