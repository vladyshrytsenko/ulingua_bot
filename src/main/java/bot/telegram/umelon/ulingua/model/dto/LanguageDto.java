package bot.telegram.umelon.ulingua.model.dto;

import lombok.Builder;

@Builder
public record LanguageDto(
    Long id,
    String countryCode,
    String unicode
) {
}
