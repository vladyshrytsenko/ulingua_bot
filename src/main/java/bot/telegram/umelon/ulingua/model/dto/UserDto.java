package bot.telegram.umelon.ulingua.model.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder(toBuilder = true)
public record UserDto(
    Long id,
    String firstname,
    String lastname,
    String username,
    String nativeLang,
    String currentLang,
    Set<LanguageDto> languages,
    LocalDateTime createdAt,
    byte dailyLimit
) {
}
