package bot.telegram.umelon.ulingua.service;

import bot.telegram.umelon.ulingua.model.dto.LanguageDto;

import java.util.List;
import java.util.Locale;

public interface LanguageService {

    void initializeLanguages();

    LanguageDto getById(long id);

    List<LanguageDto> findAll();

    LanguageDto getByCountryCode(String code);

    void deleteById(long id);

    String getAlphabet(Locale locale);
}
