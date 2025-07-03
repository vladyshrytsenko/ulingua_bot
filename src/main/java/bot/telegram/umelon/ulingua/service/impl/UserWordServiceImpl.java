package bot.telegram.umelon.ulingua.service.impl;

import bot.telegram.umelon.ulingua.model.entity.UserWord;
import bot.telegram.umelon.ulingua.model.enums.UserWordProgress;
import bot.telegram.umelon.ulingua.repository.UserWordRepository;
import bot.telegram.umelon.ulingua.service.UserWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserWordServiceImpl implements UserWordService {

    @Override
    public void addWordForUser(long userId, long wordId, UserWordProgress progress) {
        Optional<UserWord> userWordOptional = this.userWordRepository.findByUserIdAndWordId(userId, wordId);

        if (userWordOptional.isEmpty()) {
            UserWord userWord = UserWord.builder()
                .userId(userId)
                .wordId(wordId)
                .progress(progress)
                .createdAt(LocalDateTime.now())
                .build();
            this.userWordRepository.save(userWord);
        }
    }

    @Override
    public List<UserWord> findAll(long userId) {
        return this.userWordRepository.findByUserId(userId);
    }

    @Override
    public boolean isDailyLimitExceeded(long userId, long userLimit) {
        LocalDateTime currentTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime nextTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        long countPerDay = userWordRepository.countByUserIdAndProgressAndCreatedAtBetween(userId, UserWordProgress.STUDYING, currentTime, nextTime);
        return countPerDay >= userLimit;
    }

    private final UserWordRepository userWordRepository;
}
