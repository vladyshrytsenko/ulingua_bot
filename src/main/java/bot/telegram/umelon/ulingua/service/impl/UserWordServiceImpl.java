package bot.telegram.umelon.ulingua.service.impl;

import bot.telegram.umelon.ulingua.model.entity.UserWord;
import bot.telegram.umelon.ulingua.model.enums.UserWordProgress;
import bot.telegram.umelon.ulingua.repository.UserWordRepository;
import bot.telegram.umelon.ulingua.service.UserWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserWordServiceImpl implements UserWordService {

    @Override
    public void addWordForUser(long userId, long wordId, UserWordProgress progress) {
        UserWord userWord = UserWord.builder()
            .userId(userId)
            .wordId(wordId)
            .progress(progress)
            .createdAt(LocalDateTime.now())
            .build();

        this.userWordRepository.save(userWord);
    }

    @Override
    public boolean isDailyLimitExceeded(long userId, long userLimit) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime nextTime = currentTime.plusDays(1);

        long countPerDay = userWordRepository.countByUserIdAndCreatedAtBetween(userId, currentTime, nextTime);
        return countPerDay >= userLimit;
    }

    private final UserWordRepository userWordRepository;
}
