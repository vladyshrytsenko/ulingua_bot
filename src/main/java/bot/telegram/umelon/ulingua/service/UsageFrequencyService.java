package bot.telegram.umelon.ulingua.service;

public interface UsageFrequencyService {

    void increment(Long userId, Long wordId);

    void decrement(Long userId, Long wordId);

    Integer get(Long userId, Long wordId);
}
