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
        Word word = wordRepository.findById(id).orElse(null);
        return word != null ? WordMapper.MAPPER.toDto(word) : null;
    }

    @Override
    public WordDto getByOriginal(String original) {
        Word word = wordRepository.findByOriginalIgnoreCase(original).orElse(null);
        return word != null ? WordMapper.MAPPER.toDto(word) : null;
    }

    @Override
    @Transactional
    public WordDto create(WordDto requestDto) {
        Word entity = WordMapper.MAPPER.toEntity(requestDto);
        Word savedWord = wordRepository.save(entity);

        return WordMapper.MAPPER.toDto(savedWord);
    }

    private final WordRepository wordRepository;
}
