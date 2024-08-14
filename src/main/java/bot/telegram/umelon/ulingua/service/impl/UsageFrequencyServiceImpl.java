package bot.telegram.umelon.ulingua.service.impl;

import bot.telegram.umelon.ulingua.model.dto.UserWordId;
import bot.telegram.umelon.ulingua.model.entity.UserWord;
import bot.telegram.umelon.ulingua.repository.UserWordRepository;
import bot.telegram.umelon.ulingua.service.UsageFrequencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsageFrequencyServiceImpl implements UsageFrequencyService {

    private final UserWordRepository userWordRepository;

    @Override
    public void increment(Long userId, Long wordId) {
        UserWord userWord = userWordRepository.findById(new UserWordId(userId, wordId))
            .orElse(new UserWord(userId, wordId, 0));

        userWord.setUsageFrequency(userWord.getUsageFrequency() + 1);
        userWordRepository.save(userWord);
    }

    @Override
    public void decrement(Long userId, Long wordId) {
        UserWord userWord = userWordRepository.findById(new UserWordId(userId, wordId))
            .orElse(new UserWord(userId, wordId, 0));

        userWord.setUsageFrequency(Math.max(0, userWord.getUsageFrequency() - 1));
        userWordRepository.save(userWord);
    }

    @Override
    public Integer get(Long userId, Long wordId) {
        return userWordRepository.findById(new UserWordId(userId, wordId))
            .map(UserWord::getUsageFrequency)
            .orElse(0);
    }
}
