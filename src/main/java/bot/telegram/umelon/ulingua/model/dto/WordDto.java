package bot.telegram.umelon.ulingua.model.dto;

import bot.telegram.umelon.ulingua.model.entity.Word;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class WordDto {

    private Long id;
    private LanguageDto language;
    private String original;
    private String partOfSpeech;
    private String gender;
    private String tense;
    private String hieroglyphs;
    private String transliteration;
//    private List<UserDto> users;

    public static WordDto toDto(Word word) {
        return WordDto.builder()
            .id(word.getId())
            .language(LanguageDto.toDto(word.getLanguage()))
            .original(word.getOriginal())
//            .users(UserDto.toDtoList(word.getUsers()))
            .build();
    }

    public static Word toEntity(WordDto wordDto) {
        return Word.builder()
            .id(wordDto.getId())
            .language(LanguageDto.toEntity(wordDto.getLanguage()))
            .original(wordDto.getOriginal())
//            .users(UserDto.toEntityList(users))
            .build();
    }
}
