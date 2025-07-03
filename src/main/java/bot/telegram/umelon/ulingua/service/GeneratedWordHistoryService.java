package bot.telegram.umelon.ulingua.service;

import bot.telegram.umelon.ulingua.model.dto.GeneratedWordHistoryDto;
import bot.telegram.umelon.ulingua.model.entity.redis.GeneratedWordHistory;

import java.util.List;

public interface GeneratedWordHistoryService {

    GeneratedWordHistoryDto findFirstByCountryCode(String countryCode);
    List<GeneratedWordHistoryDto> findAllByCountryCode(String countryCode);
    void saveAll(List<GeneratedWordHistory> entities);
    void deleteByOriginal(String original);
}
