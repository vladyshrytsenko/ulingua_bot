package bot.telegram.umelon.ulingua.service.impl;

import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.dto.WordDto;
import bot.telegram.umelon.ulingua.model.entity.User;
import bot.telegram.umelon.ulingua.model.entity.Word;
import bot.telegram.umelon.ulingua.repository.WordRepository;
import bot.telegram.umelon.ulingua.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public WordDto save(Word word) {
//        Optional<Word> existingWordOpt = wordRepository.findByChatId(user.getChatId());
//        Word savedWord;
//
//        if (existingWordOpt.isPresent()) {
//            Word existingWord = existingWordOpt.get();
//
//            if (word.getGender() != null) {
//                existingWord.setGender(word.getGender());
//            }
//            if (word.getHieroglyphs() != null) {
//                existingWord.setHieroglyphs(word.getHieroglyphs());
//            }
//            if (word.getOriginal() != null) {
//                existingWord.setOriginal(word.getOriginal());
//            }
//            if (word.getLanguage() != null) {
//                existingWord.setLanguage(word.getLanguage());
//            }
//            if (word.getTense() != null) {
//                existingWord.setTense(word.getTense());
//            }
//            if (word.getTransliteration() != null) {
//                existingWord.setTransliteration(word.getTransliteration());
//            }
//            if (word.getPartOfSpeech() != null) {
//                existingWord.setPartOfSpeech(word.getPartOfSpeech());
//            }
//
//            savedWord = wordRepository.save(existingWord);
//        } else {
//            savedWord = wordRepository.save(word);
//        }
//
//        return WordDto.toDto(savedWord);
        return null;
    }
}
