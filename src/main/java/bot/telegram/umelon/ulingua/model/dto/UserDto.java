package bot.telegram.umelon.ulingua.model.dto;

import bot.telegram.umelon.ulingua.model.entity.Language;
import bot.telegram.umelon.ulingua.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private Long chatId;
    private String firstName;
    private String lastName;
    private String username;
    private String nativeLang;
    private String currentLang;
    private Set<LanguageDto> languages;
    private Date createdAt;

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        Set<LanguageDto> languagesDto = new HashSet<>();
        if (user.getLanguages() != null) {
            languagesDto = user.getLanguages().stream()
                .map(LanguageDto::toDto)
                .collect(Collectors.toSet());
        }

        return UserDto.builder()
            .id(user.getId())
            .chatId(user.getChatId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .username(user.getUsername())
            .nativeLang(user.getNativeLang())
            .currentLang(user.getCurrentLang())
            .languages(languagesDto)
            .createdAt(user.getCreatedAt())
            .build();
    }

    public static User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        Set<Language> languages = new HashSet<>();
        if (userDto.getLanguages() != null) {
            languages = userDto.getLanguages().stream()
                .map(LanguageDto::toEntity)
                .collect(Collectors.toSet());
        }

        return User.builder()
            .id(userDto.getId())
            .chatId(userDto.getChatId())
            .firstName(userDto.getFirstName())
            .lastName(userDto.getLastName())
            .username(userDto.getUsername())
            .nativeLang(userDto.getNativeLang())
            .currentLang(userDto.getCurrentLang())
            .languages(languages)
            .createdAt(userDto.getCreatedAt())
            .build();
    }
}
