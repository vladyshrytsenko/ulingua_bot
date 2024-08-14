package bot.telegram.umelon.ulingua.model.dto;

import bot.telegram.umelon.ulingua.model.entity.Language;
//import bot.telegram.umelon.ulingua.model.entity.Translation;
import bot.telegram.umelon.ulingua.model.entity.Word;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Builder
public class TranslationDto {

    private Long id;
    private WordDto word;
    private String translation;
    private String context;
    private List<String> sentences;
    private String languageCode;

//    public static TranslationDto toDto(Translation translation) {
//        return TranslationDto.builder()
//            .id(translation.getId())
//            .word(WordDto.toDto(translation.getWord()))
//            .translation(translation.getTranslation())
//            .context(translation.getContext())
//            .sentences(convertStringToList(translation.getSentences()))
//            .languageCode(translation.getLanguageCode())
//            .build();
//    }
//
//    public static List<TranslationDto> toDtoList(List<Translation> translations) {
//        if (translations == null || translations.isEmpty()) {
//            return List.of();
//        }
//
//        return translations.stream()
//            .map(TranslationDto::toDto)
//            .collect(Collectors.toList());
//    }
//
//    public static Translation toEntity(TranslationDto translationDto, Word word) {
//        return Translation.builder()
//            .id(translationDto.getId())
//            .word(word)
//            .translation(translationDto.getTranslation())
//            .context(translationDto.getContext())
//            .sentences(convertListToString(translationDto.getSentences()))
//            .languageCode(translationDto.getLanguageCode())
//            .build();
//    }
//
//    public static List<Translation> toEntityList(List<TranslationDto> translationDtos, Word wordEntity) {
//        if (translationDtos == null || translationDtos.isEmpty()) {
//            return List.of();
//        }
//
//        return translationDtos.stream()
//            .map((TranslationDto translationDto) -> toEntity(translationDto, wordEntity))
//            .collect(Collectors.toList());
//    }

    private static List<String> convertStringToList(String sentences) {
        try {
            return objectMapper.readValue(sentences, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static String convertListToString(List<String> sentences) {
        try {
            return objectMapper.writeValueAsString(sentences);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "[]";
        }
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();
}
