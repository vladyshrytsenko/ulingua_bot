package bot.telegram.umelon.ulingua.service.impl;

import bot.telegram.umelon.ulingua.model.dto.WordDto;
import bot.telegram.umelon.ulingua.model.entity.Word;
import bot.telegram.umelon.ulingua.model.mapper.WordMapper;
import bot.telegram.umelon.ulingua.repository.WordRepository;
import bot.telegram.umelon.ulingua.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WordServiceImpl implements WordService {

    @Override
    public WordDto getById(long id) {
        Word word = this.wordRepository.findById(id).orElse(null);
        return word != null ? WordMapper.MAPPER.toDto(word) : null;
    }

    @Override
    public Word getByOriginal(String original) {
        return this.wordRepository.findByOriginalIgnoreCase(original).orElse(null);
    }

    @Override
    @Transactional
    public WordDto create(Word word) {
        Word savedWord = this.wordRepository.save(word);
        return WordMapper.MAPPER.toDto(savedWord);
    }

    private final WordRepository wordRepository;
}
