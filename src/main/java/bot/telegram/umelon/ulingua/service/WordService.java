package bot.telegram.umelon.ulingua.service;

import bot.telegram.umelon.ulingua.model.dto.WordDto;
import bot.telegram.umelon.ulingua.model.entity.Word;

public interface WordService {

    WordDto getById(long id);

    WordDto create(Word word);

}
