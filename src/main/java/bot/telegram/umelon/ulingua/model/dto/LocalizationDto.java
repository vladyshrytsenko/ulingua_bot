package bot.telegram.umelon.ulingua.model.dto;

import lombok.Builder;

@Builder
public record LocalizationDto(
    Long id,
    Long chatId,
    String langCode
) {
}
