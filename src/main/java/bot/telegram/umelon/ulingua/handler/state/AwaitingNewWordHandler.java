package bot.telegram.umelon.ulingua.handler.state;

import bot.telegram.umelon.ulingua.handler.StateHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.dto.WordDto;
import bot.telegram.umelon.ulingua.model.entity.Word;
import bot.telegram.umelon.ulingua.model.enums.UserWordProgress;
import bot.telegram.umelon.ulingua.service.LanguageService;
import bot.telegram.umelon.ulingua.service.OpenAIService;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.service.UserWordService;
import bot.telegram.umelon.ulingua.service.WordService;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static java.lang.String.*;

@Component
@RequiredArgsConstructor
public class AwaitingNewWordHandler implements StateHandler {

    @Override
    public void handle(long chatId, String messageText, UserDto currentUser, LocalMessages localMessages) {
        String word = messageText.split(" ")[0];

        String langList = currentUser.getLanguages().stream()
            .map(LanguageDto::getUnicode)
            .collect(Collectors.joining(","));

        String chatCompletion = this.openAIService.getChatCompletion(format(
            "Provide information about the word '%s' in %s language. Answer in JSON format with the following fields: " +
            "'exists' (yes or no). If 'no' then in any language from %s. But if 'yes', then add the 'language_code' " +
            "(2 digits in capital letters)'.", word, currentUser.getCurrentLang(), langList
        ));

        JsonNode wordInfoJsonNode = null;
        try {
            wordInfoJsonNode = this.objectMapper.readTree(chatCompletion);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (wordInfoJsonNode.get("exists").textValue().equalsIgnoreCase("yes")) {
            this.telegramUtils.sendMessage(chatId, format(localMessages.get("message.adding_word_to_study_list"), messageText));

            String languageCode;
            if (wordInfoJsonNode.get("language_code") == null) {
                languageCode = this.openAIService.getChatCompletion(
                    format("'language_code' was empty, although such a word exists. Generate again choosing one " +
                           "from this list %s in which this word exists. Then output only the code (2 characters).", langList
                    )
                );

            } else {
                languageCode = wordInfoJsonNode.get("language_code").textValue();
            }

            LanguageDto byCountryCode = this.languageService.getByCountryCode(languageCode);

            Word newWord = Word.builder()
                .language(LanguageDto.toEntity(byCountryCode))
                .original(word)
                .build();
            WordDto createdWord = this.wordService.create(newWord);
            // todo: needs to be checked what the Progress it should be
            this.userWordService.addWordForUser(
                currentUser.getId(),
                createdWord.getId(),
                UserWordProgress.STUDYING
            );

            this.telegramUtils.sendMessage(chatId, localMessages.get("message.done"));
        } else {
            this.telegramUtils.sendMessage(chatId, localMessages.get("message.word_not_exist"));
        }

        this.userService.setUserState(chatId, null);
    }

    private final UserService userService;
    private final WordService wordService;
    private final LanguageService languageService;
    private final TelegramUtils telegramUtils;
    private final OpenAIService openAIService;
    private final ObjectMapper objectMapper;
    private final UserWordService userWordService;
}
