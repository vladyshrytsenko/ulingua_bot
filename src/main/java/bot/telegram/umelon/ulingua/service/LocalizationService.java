package bot.telegram.umelon.ulingua.service;

import bot.telegram.umelon.ulingua.model.dto.LocalizationDto;

public interface LocalizationService {

    LocalizationDto getById(long id);
    LocalizationDto getByChatId(long chatId);
    void setBotLanguage(long chatId, String langCode);

}
