package bot.telegram.umelon.ulingua.service.impl;

import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.entity.UserWord;
import bot.telegram.umelon.ulingua.model.enums.UserWordProgress;
import bot.telegram.umelon.ulingua.repository.UserWordRepository;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.service.UserWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserWordServiceImpl implements UserWordService {

    @Override
    public void addWordForUser(long chatId, long wordId, UserWordProgress progress) {
        UserDto userById = this.userService.getByChatId(chatId);

        UserWord userWord = UserWord.builder()
            .userId(chatId)
            .wordId(wordId)
            .progress(progress)
            .build();

        this.userWordRepository.save(userWord);
    }

    @Override
    public void removeWord(long chatId, long wordId) {

    }

    @Override
    public boolean isDailyLimitExceeded(long userId, long userLimit) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime nextTime = currentTime.plusDays(1);

        long countPerDay = userWordRepository.countByUserIdAndCreatedAtBetween(userId, currentTime, nextTime);
        return countPerDay >= userLimit;
    }

    private final UserWordRepository userWordRepository;
    private final UserService userService;
}
