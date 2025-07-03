package bot.telegram.umelon.ulingua.model.dto;

import lombok.Builder;

@Builder
public record GeneratedWordHistoryDto(
    Long userId,
    String countryCode,
    String original
) {
}
