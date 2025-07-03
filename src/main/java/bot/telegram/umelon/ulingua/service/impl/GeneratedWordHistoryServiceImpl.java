package bot.telegram.umelon.ulingua.service.impl;

import bot.telegram.umelon.ulingua.model.dto.GeneratedWordHistoryDto;
import bot.telegram.umelon.ulingua.model.entity.redis.GeneratedWordHistory;
import bot.telegram.umelon.ulingua.model.mapper.GeneratedWordHistoryMapper;
import bot.telegram.umelon.ulingua.repository.GeneratedWordHistoryRepository;
import bot.telegram.umelon.ulingua.service.GeneratedWordHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class GeneratedWordHistoryServiceImpl implements GeneratedWordHistoryService {

    @Override
    public GeneratedWordHistoryDto findFirstByCountryCode(String countryCode) {
        GeneratedWordHistory word = this.generatedWordHistoryRepository.findFirstByCountryCode(countryCode).orElse(null);
        return word != null ? GeneratedWordHistoryMapper.MAPPER.toDto(word): null;
    }

    @Override
    public List<GeneratedWordHistoryDto> findAllByCountryCode(String countryCode) {
        Iterable<GeneratedWordHistory> wordHistoryIterable = this.generatedWordHistoryRepository.findAllByCountryCode(countryCode);

        return StreamSupport.stream(wordHistoryIterable.spliterator(), false)
            .map(GeneratedWordHistoryMapper.MAPPER::toDto)
            .toList();
    }

    @Override
    public void saveAll(List<GeneratedWordHistory> entities) {
        this.generatedWordHistoryRepository.saveAll(entities);
    }

    @Override
    public void deleteByOriginal(String original) {
        this.generatedWordHistoryRepository.findByOriginal(original)
            .ifPresent(this.generatedWordHistoryRepository::delete);
    }

    private final GeneratedWordHistoryRepository generatedWordHistoryRepository;
}
