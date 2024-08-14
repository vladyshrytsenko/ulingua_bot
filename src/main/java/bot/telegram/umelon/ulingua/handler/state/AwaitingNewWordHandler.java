package bot.telegram.umelon.ulingua.handler.state;

import bot.telegram.umelon.ulingua.handler.StateHandler;
import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.dto.WordDto;
import bot.telegram.umelon.ulingua.model.entity.Word;
import bot.telegram.umelon.ulingua.service.LanguageService;
import bot.telegram.umelon.ulingua.service.OpenAIService;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.service.WordService;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AwaitingNewWordHandler implements StateHandler {

    private final UserService userService;
    private final WordService wordService;
    private final LanguageService languageService;
    private final TelegramUtils telegramUtils;
    private final OpenAIService openAIService;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(long chatId, String messageText, UserDto currentUserDto) {

        String word = messageText.split(" ")[0];

        String langList = currentUserDto.getLanguages().stream()
            .map(LanguageDto::getUnicode)
            .collect(Collectors.joining(","));

        String chatCompletion = openAIService.getChatCompletion(String.format(
            "Provide information about the word '%s' in %s language. Answer in JSON format with the following fields: " +
            "'exists' (yes or no) if 'no' then in any language from %s, 'language code (2 digits in capital letters)'.",
            word,
            currentUserDto.getCurrentLang(),
            langList
        ));

        JsonNode wordInfoJsonNode = null;
        try {
            wordInfoJsonNode = objectMapper.readTree(chatCompletion);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (wordInfoJsonNode.get("exists").textValue().equalsIgnoreCase("yes")) {
            telegramUtils.sendMessage(chatId, String.format("Записую '%s' до списку на вивчення...", messageText));
            String languageCode = wordInfoJsonNode.get("language_code").textValue();
            if (languageCode == null) {
                languageCode = currentUserDto.getCurrentLang();
            }
            LanguageDto byCountryCode = languageService.getByCountryCode(languageCode);

            Word newWord = Word.builder()
                .language(LanguageDto.toEntity(byCountryCode))
                .original(word)
                .build();
            WordDto createdWord = wordService.create(newWord);
            userService.addWordForUser(currentUserDto.getId(), createdWord.getId());

            telegramUtils.sendMessage(chatId, "Готово");
        } else {
            telegramUtils.sendMessage(chatId, "Такого слова не існує");
        }

        userService.setUserState(chatId, null);
    }
}

