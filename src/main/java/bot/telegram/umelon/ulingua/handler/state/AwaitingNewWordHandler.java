package bot.telegram.umelon.ulingua.handler.state;

import bot.telegram.umelon.ulingua.handler.StateHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.dto.WordDto;
import bot.telegram.umelon.ulingua.model.entity.UserWord;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class AwaitingNewWordHandler implements StateHandler {

    @Override
    public void handle(long userId, String messageText, UserDto currentUser, LocalMessages localMessages) {
        String currentLang = currentUser.currentLang();

        String promptTemplate = """
            You are an automated API endpoint. Your sole function is to return a raw, minified JSON string. You MUST NOT provide any explanations, apologies, or any text outside of the JSON structure itself.
            
            TASK:
            1. Parse the user's input text which contains words to learn and words already known.
            2. The target language for word validation is: %s.
            3. Words from the line starting with '1 -' must go into the `KNOWN` array.
            4. Words from the line starting with '2 -' must go into the `LEARNING` array.
            5. From each list, include ONLY words that are valid and exist in the specified language.
            6. If a line is missing or contains no valid words, the corresponding JSON array must be empty (`[]`).
            7. Your final response MUST BE ONLY the raw minified JSON text. Do NOT wrap it in Markdown code blocks like ```json or ```.
            
            --- EXAMPLES ---
            
            Example 1:
            User Input:
            1 - sky, water, qwerty
            2 - sun, moon
            Language: English
            JSON Output:
            {"KNOWN":["sky","water"],"LEARNING":["sun","moon"]}
            
            Example 2:
            User Input:
            1 - hola, amigo
            Language: Spanish
            JSON Output:
            {"KNOWN":["hola","amigo"],"LEARNING":[]}
            
            Example 3:
            User Input:
            2 - Apfel, Haus, nonword
            Language: German
            JSON Output:
            {"KNOWN":[],"LEARNING":["Apfel","Haus"]}
            
            --- TASK TO PERFORM ---
            
            User Input:
            %s
            Language: %s
            JSON Output:
            """;

        String chatCompletion = this.generativeAiService.chatCompletion(
            AiProvider.GEMINI, promptTemplate.formatted(currentLang, messageText, currentLang)
        );

        // Add a simple cleanup in case the model makes a mistake
        chatCompletion = chatCompletion.replace("```json", "").replace("```", "").trim();

        JsonNode wordsJsonNode;
        try {
            wordsJsonNode = this.objectMapper.readTree(chatCompletion);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }

        ArrayNode knownArrayNode = (ArrayNode) wordsJsonNode.get(UserWordProgress.KNOWN.name());
        List<String> knownList;
        try {
            knownList = objectMapper.readValue(knownArrayNode.toPrettyString(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
        ArrayNode learningArrayNode = (ArrayNode) wordsJsonNode.get(UserWordProgress.LEARNING.name());
        List<String> learningList;
        try {
            learningList = objectMapper.readValue(learningArrayNode.toPrettyString(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }

        if (!knownList.isEmpty() || !learningList.isEmpty()) {
            List<UserWord> userWords = userWordService.findAll(currentUser.id());
            knownList.removeIf(knownWord ->
                userWords.stream()
                    .map(word -> wordService.getById(word.getWordId()))
                    .filter(word -> word.language().countryCode().equals(currentUser.currentLang()))
                    .anyMatch(word -> knownWord.equalsIgnoreCase(word.original()))
            );
            learningList.removeIf(learningWord ->
                userWords.stream()
                    .map(word -> wordService.getById(word.getWordId()))
                    .filter(word -> word.language().countryCode().equals(currentUser.currentLang()))
                    .anyMatch(word -> learningWord.equalsIgnoreCase(word.original()))
            );

            List<String> mergedList = Stream.concat(knownList.stream(), learningList.stream()).toList();
            if (mergedList.isEmpty()) {
                this.telegramUtils.sendMessage(userId, "Нажаль, нових слів не виявлено", true);
                return;
            }
            this.telegramUtils.sendMessage(
                userId,
                localMessages.get("message.adding_word_to_study_list").formatted(mergedList.toString()),
                true
            );

            LanguageDto countryCode = this.languageService.getByCountryCode(currentLang);

            knownList.forEach(knownWord -> {
                WordDto wordRequest = WordDto.builder()
                    .language(countryCode)
                    .original(knownWord)
                    .build();
                WordDto createdWord = this.wordService.create(wordRequest);

                this.userWordService.addWordForUser(currentUser.id(), createdWord.id(), UserWordProgress.KNOWN);
            });
            learningList.forEach(learningWord -> {
                WordDto wordRequest = WordDto.builder()
                    .language(countryCode)
                    .original(learningWord)
                    .build();
                WordDto createdWord = this.wordService.create(wordRequest);

                this.userWordService.addWordForUser(currentUser.id(), createdWord.id(), UserWordProgress.LEARNING);
            });

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
