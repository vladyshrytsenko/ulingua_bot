package bot.telegram.umelon.ulingua.service.impl;

import bot.telegram.umelon.ulingua.model.dto.WordDto;
import bot.telegram.umelon.ulingua.model.entity.Word;
import bot.telegram.umelon.ulingua.repository.WordRepository;
import bot.telegram.umelon.ulingua.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WordServiceImpl implements WordService {

    private final WordRepository wordRepository;

    @Override
    public WordDto getById(long id) {
        Word word = wordRepository.findById(id).orElse(null);
        return word != null ? WordDto.toDto(word) : null;
    }

    @Override
    @Transactional
    public WordDto create(Word word) {
        Optional<Word> existingWordOptional = wordRepository.findByOriginal(word.getOriginal());
        Word savedWord;

        if (existingWordOptional.isPresent()) {
            Word existingWord = existingWordOptional.get();
            existingWord.setOriginal(word.getOriginal());

            savedWord = wordRepository.save(existingWord);

        } else {
            Word newWord = Word.builder()
                .original(word.getOriginal())
                .language(word.getLanguage())
                .build();

            savedWord = wordRepository.save(newWord);
        }

        return WordDto.toDto(savedWord);
    }
}
