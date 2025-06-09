package bot.telegram.umelon.ulingua.model.dto;

import bot.telegram.umelon.ulingua.model.entity.Localization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LocalizationDto {

    private Long id;
    private Long chatId;
    private String langCode;

    public static LocalizationDto toDto(Localization localization) {
        return LocalizationDto.builder()
            .id(localization.getId())
            .chatId(localization.getChatId())
            .langCode(localization.getLangCode())
            .build();
    }

    public static Localization toEntity(LocalizationDto localizationDto) {
        return Localization.builder()
            .id(localizationDto.getId())
            .chatId(localizationDto.getChatId())
            .langCode(localizationDto.getLangCode())
            .build();
    }
}
