package bot.telegram.umelon.ulingua.handler.state;

import bot.telegram.umelon.ulingua.handler.StateHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.dto.WordDto;
import bot.telegram.umelon.ulingua.model.entity.Word;
import bot.telegram.umelon.ulingua.model.enums.AiProvider;
import bot.telegram.umelon.ulingua.model.enums.UserState;
import bot.telegram.umelon.ulingua.model.enums.UserWordProgress;
import bot.telegram.umelon.ulingua.service.LanguageService;
import bot.telegram.umelon.ulingua.service.GenerativeAiService;
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

@Component
@RequiredArgsConstructor
public class AwaitingNewWordHandler implements StateHandler {

    @Override
    public void handle(long userId, String messageText, UserDto currentUser, LocalMessages localMessages) {
        String word = messageText.split(" ")[0];

        String langList = currentUser.getLanguages().stream()
            .map(LanguageDto::getUnicode)
            .collect(Collectors.joining(","));

        String chatCompletion = this.generativeAiService.chatCompletion(
            AiProvider.GEMINI,
            "Provide information about the word '%s' in %s language. ".formatted(word, currentUser.getCurrentLang() +
            "Respond ONLY with a valid minified JSON object (no extra formatting, no ```json, no trailing spaces/newlines). " +
            "Required fields: 'exists' (yes/no). If 'exists':'yes', add 'language_code' (2 uppercase letters). " +
            "Example of valid response: {\"exists\":\"yes\",\"language_code\":\"ES\"} " +
            "Important: Do NOT include any other text, symbols, or formatting outside the JSON object."
        ));

        JsonNode wordInfoJsonNode = null;
        try {
            wordInfoJsonNode = this.objectMapper.readTree(chatCompletion);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (wordInfoJsonNode.get("exists").textValue().equalsIgnoreCase("yes")) {
            this.telegramUtils.sendMessage(
                userId,
                localMessages.get("message.adding_word_to_study_list").formatted(messageText),
                true
            );

            String languageCode;
            if (wordInfoJsonNode.get("language_code") == null) {
                languageCode = this.generativeAiService.chatCompletion(
                    AiProvider.GEMINI,
                    "'language_code' was empty, although such a word exists. Generate again choosing one " +
                    "from this list %s in which this word exists. Then output only the code (2 characters).".formatted(langList)
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

            this.userWordService.addWordForUser(
                currentUser.getId(),
                createdWord.getId(),
                UserWordProgress.STUDYING
            );

            this.telegramUtils.sendMessage(userId, localMessages.get("message.done"), true);
        } else {
            this.telegramUtils.sendMessage(userId, localMessages.get("message.word_not_exist"), true);
        }

        this.userService.setUserState(userId, UserState.AWAITING_NEW_WORD);
    }

    private final UserService userService;
    private final WordService wordService;
    private final LanguageService languageService;
    private final TelegramUtils telegramUtils;
    private final GenerativeAiService generativeAiService;
    private final ObjectMapper objectMapper;
    private final UserWordService userWordService;
}
