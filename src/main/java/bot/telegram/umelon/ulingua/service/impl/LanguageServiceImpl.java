package bot.telegram.umelon.ulingua.service.impl;

import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.entity.Language;
import bot.telegram.umelon.ulingua.repository.LanguageRepository;
import bot.telegram.umelon.ulingua.utils.CountryFlagUtil;
import bot.telegram.umelon.ulingua.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;
    private final CountryFlagUtil countryFlagUtil;

    @Override
    public void initializeLanguages() {
        if (languageRepository.count() == 0) {
            Map<String, String> countryMapping = countryFlagUtil.getCountryMapping();
            List<Language> languageList = new ArrayList<>();

            countryMapping.forEach((country, unicode) -> {
                languageList.add(new Language(country, unicode));
            });

            languageRepository.saveAll(languageList);
        }
    }

    @Override
    public LanguageDto getById(long id) {
        Language language = languageRepository.findById(id).orElse(null);
        return language != null ? LanguageDto.toDto(language) : null;
    }

    @Override
    public List<LanguageDto> findAll() {
        List<Language> languages = languageRepository.findAll();
        return LanguageDto.toDtoList(languages);
    }


    @Override
    public LanguageDto getByCountryCode(String code) {
        Optional<Language> languageOptional = languageRepository.getByCountryCode(code);
        return languageOptional.map(LanguageDto::toDto).orElse(null);
    }

    @Override
    public void deleteById(long id) {
        if (languageRepository.existsById(id)) {
            languageRepository.deleteById(id);
        } else {
            throw new RuntimeException("Language not found");
        }
    }

    public String getAlphabet(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("ABC/alphabet", locale);

        if (locale.equals(new Locale("ja", "JP"))) {
            String hiragana = bundle.getString("hiragana");
            String katakana = bundle.getString("katakana");
            return "Hiragana:\n" + hiragana + "\n\n" + "Katakana:\n" + katakana;
        } else {
            return bundle.getString("value");
        }
    }

}
