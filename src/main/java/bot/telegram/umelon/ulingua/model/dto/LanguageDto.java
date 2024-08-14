package bot.telegram.umelon.ulingua.model.dto;

import bot.telegram.umelon.ulingua.model.entity.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Builder
public class LanguageDto {

    private Long id;
    private String countryCode;
    private String unicode;

    public static LanguageDto toDto(Language language) {
        if (language == null) {
            return null;
        }

        return LanguageDto.builder()
            .id(language.getId())
            .countryCode(language.getCountryCode())
            .unicode(language.getUnicode())
            .build();
    }

    public static List<LanguageDto> toDtoList(List<Language> languages) {
        if (languages == null || languages.isEmpty()) {
            return List.of();
        }

        return languages.stream()
            .map(LanguageDto::toDto)
            .collect(Collectors.toList());
    }

    public static Language toEntity(LanguageDto languageDto) {
        if (languageDto == null) {
            return null;
        }

        return Language.builder()
            .id(languageDto.getId())
            .countryCode(languageDto.getCountryCode())
            .unicode(languageDto.getUnicode())
            .build();
    }

    public static List<Language> toEntityList(List<LanguageDto> languageDtos) {
        if (languageDtos == null || languageDtos.isEmpty()) {
            return List.of();
        }

        return languageDtos.stream()
            .map(LanguageDto::toEntity)
            .collect(Collectors.toList());
    }
}
