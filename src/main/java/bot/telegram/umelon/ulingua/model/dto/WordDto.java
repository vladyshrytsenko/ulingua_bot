package bot.telegram.umelon.ulingua.model.dto;

import lombok.Builder;

@Builder
public record WordDto(
    Long id,
    LanguageDto language,
    String original
) {
}
