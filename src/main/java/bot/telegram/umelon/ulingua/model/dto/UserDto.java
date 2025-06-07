package bot.telegram.umelon.ulingua.model.dto;

import bot.telegram.umelon.ulingua.model.entity.Language;
import bot.telegram.umelon.ulingua.model.entity.User;
import bot.telegram.umelon.ulingua.model.entity.Word;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String firstname;
    private String lastname;
    private String username;
    private String nativeLang;
    private String currentLang;
    private String localization;
    private Set<LanguageDto> languages;
    private Date createdAt;
    private byte dailyLimit;

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
            .firstname(user.getFirstname())
            .lastname(user.getLastname())
            .username(user.getUsername())
            .nativeLang(user.getNativeLang())
            .currentLang(user.getCurrentLang())
            .localization(user.getLocalization())
            .languages(languagesDto)
            .createdAt(user.getCreatedAt())
            .dailyLimit(user.getDailyLimit())
            .build();
    }

    public static List<UserDto> toDtoList(List<User> users) {
        if (users == null || users.isEmpty()) {
            return List.of();
        }

        return users.stream()
            .map(UserDto::toDto)
            .collect(Collectors.toList());
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
            .firstname(userDto.getFirstname())
            .lastname(userDto.getLastname())
            .username(userDto.getUsername())
            .nativeLang(userDto.getNativeLang())
            .currentLang(userDto.getCurrentLang())
            .localization(userDto.getLocalization())
            .languages(languages)
            .createdAt(userDto.getCreatedAt())
            .dailyLimit(userDto.getDailyLimit())
            .build();
    }

    public static List<User> toEntityList(List<UserDto> userDtos) {
        if (userDtos == null || userDtos.isEmpty()) {
            return List.of();
        }

        return userDtos.stream()
            .map(UserDto::toEntity)
            .collect(Collectors.toList());
    }
}
