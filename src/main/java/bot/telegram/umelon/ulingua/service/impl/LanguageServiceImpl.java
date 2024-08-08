package bot.telegram.umelon.ulingua.service.impl;

import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.entity.Language;
import bot.telegram.umelon.ulingua.repository.LanguageRepository;
import bot.telegram.umelon.ulingua.utils.CountryFlagUtil;
import bot.telegram.umelon.ulingua.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

}
