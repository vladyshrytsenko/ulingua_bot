package bot.telegram.umelon.ulingua.service;

import bot.telegram.umelon.ulingua.model.dto.WordDto;

public interface WordService {

    WordDto getById(long id);
    WordDto getByOriginal(String original);
    WordDto create(WordDto requestDto);
}
