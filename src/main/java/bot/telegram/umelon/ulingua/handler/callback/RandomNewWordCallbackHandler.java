package bot.telegram.umelon.ulingua.handler.callback;

import bot.telegram.umelon.ulingua.handler.CallbackHandler;
import bot.telegram.umelon.ulingua.model.ButtonData;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.GeneratedWordHistoryDto;
import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.dto.WordDto;
import bot.telegram.umelon.ulingua.model.entity.redis.GeneratedWordHistory;
import bot.telegram.umelon.ulingua.model.enums.AiProvider;
import bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum;
import bot.telegram.umelon.ulingua.model.enums.UserWordProgress;
import bot.telegram.umelon.ulingua.service.GeneratedWordHistoryService;
import bot.telegram.umelon.ulingua.service.LanguageService;
import bot.telegram.umelon.ulingua.service.GenerativeAiService;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.service.UserWordService;
import bot.telegram.umelon.ulingua.service.WordService;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum.*;

@Component
@RequiredArgsConstructor
public class RandomNewWordCallbackHandler implements CallbackHandler {

    @Override
    public void handle(CallbackQuery callbackQuery, LocalMessages localMessages) {
        MaybeInaccessibleMessage message = callbackQuery.getMessage();

        UserDto currentUser = this.userService.getById(message.getChatId());
        LanguageDto byCountryCode = this.languageService.getByCountryCode(currentUser.currentLang());

        if (callbackQuery.getData().endsWith(RANDOM_NEW_WORD.getValue())) {
            byte dailyLimit = currentUser.dailyLimit();

            if (this.userWordService.isDailyLimitExceeded(currentUser.id(), dailyLimit)) {
                this.telegramUtils.sendMessage(currentUser.id(), "Daily limit exceeded!", false);
            } else {
                this.prepareWordHistory(currentUser);

                GeneratedWordHistoryDto wordFirst = this.generatedWordHistoryService.findFirstByCountryCode(currentUser.currentLang());

                List<ButtonData> buttons = getButtonDataList(wordFirst.original());
                this.telegramUtils.sendEditMessageTextWithInlineKeyboard(
                    message.getChatId(),
                    message.getMessageId(),
                    wordFirst.original(),
                    buttons
                );
            }

        } else if (callbackQuery.getData().endsWith(RANDOM_NEW_WORD_ALREADY_KNOW.getValue())) {
            String wordStr = RANDOM_NEW_WORD_ALREADY_KNOW.getDescription();
            WordDto wordByOriginal = this.wordService.getByOriginal(RANDOM_NEW_WORD_ALREADY_KNOW.getDescription());

            if (wordByOriginal == null) {
                WordDto wordRequest = WordDto.builder()
                    .original(wordStr)
                    .language(byCountryCode)
                    .build();
                wordByOriginal = this.wordService.create(wordRequest);
            }
            this.userWordService.addWordForUser(
                message.getChatId(),
                wordByOriginal.id(),
                UserWordProgress.KNEW
            );

            this.generatedWordHistoryService.deleteByOriginal(wordStr);
            this.prepareWordHistory(currentUser);

            this.telegramUtils.sendDeleteMessageRequest(message.getChatId(), message.getMessageId());

            callbackQuery.setData(RANDOM_NEW_WORD.getValue());
            this.handle(callbackQuery, localMessages);

        } else if (callbackQuery.getData().endsWith(RANDOM_NEW_WORD_FOR_STUDY.getValue())) {
            String wordStr = RANDOM_NEW_WORD_FOR_STUDY.getDescription();
            WordDto wordByOriginal = this.wordService.getByOriginal(RANDOM_NEW_WORD_FOR_STUDY.getDescription());

            if (wordByOriginal == null) {
                WordDto wordRequest = WordDto.builder()
                    .original(wordStr)
                    .language(byCountryCode)
                    .build();
                wordByOriginal = this.wordService.create(wordRequest);
            }
            this.userWordService.addWordForUser(
                message.getChatId(),
                wordByOriginal.id(),
                UserWordProgress.STUDYING
            );

            this.generatedWordHistoryService.deleteByOriginal(wordStr);
            this.prepareWordHistory(currentUser);

            callbackQuery.setData(RANDOM_NEW_WORD.getValue());
            this.handle(callbackQuery, localMessages);

        } else if (callbackQuery.getData().endsWith(RANDOM_NEW_WORD_NOT_INTERESTING.getValue())) {
            String wordStr = RANDOM_NEW_WORD_NOT_INTERESTING.getDescription();
            WordDto wordByOriginal = this.wordService.getByOriginal(RANDOM_NEW_WORD_NOT_INTERESTING.getDescription());

            if (wordByOriginal == null) {
                WordDto wordRequest = WordDto.builder()
                    .original(wordStr)
                    .language(byCountryCode)
                    .build();
                wordByOriginal = this.wordService.create(wordRequest);
            }
            this.userWordService.addWordForUser(
                message.getChatId(),
                wordByOriginal.id(),
                UserWordProgress.NOT_INTERESTED
            );

            this.generatedWordHistoryService.deleteByOriginal(wordStr);
            this.prepareWordHistory(currentUser);

            callbackQuery.setData(RANDOM_NEW_WORD.getValue());
            this.handle(callbackQuery, localMessages);
        }
    }

    private void prepareWordHistory(UserDto currentUser) {
        List<GeneratedWordHistoryDto> generatedWords = this.generatedWordHistoryService.findAllByCountryCode(currentUser.currentLang());
        if (generatedWords.isEmpty()) {
            List<String> userWords = userWordService.findAll(currentUser.id()).stream()
                .map(uw -> {
                    WordDto word = wordService.getById(uw.getWordId());
                    return word.original();
                }).toList();

            int maxKnownWords = 200;
            int leftLimit = userWords.size() > maxKnownWords ? new Random().nextInt(userWords.size() - maxKnownWords + 1) : 0;
            int rightLimit = Math.min(leftLimit + maxKnownWords, userWords.size());
            List<String> userWordSimplifiedList = userWords.subList(leftLimit, rightLimit);

            String chatCompletion = this.generativeAiService.chatCompletion(
                AiProvider.GEMINI,
                "I am learning %s. ".formatted(currentUser.currentLang()) +
                "Below is a sample of words I already know: %s. ".formatted(userWordSimplifiedList) +
                "Based on that, give me a new list of 20 **very commonly used**, everyday words that are **concrete and practical** " +
                "(such as basic nouns, verbs, or adjectives) that a beginner would find **immediately useful**. " +
                "Avoid repeating any of the words I already know. No explanation, translation, or additional context. " +
                "Respond only with a comma-separated list of exactly 20 words and nothing else. " +
                "Example of a valid response: word1, word2, ..., word20"
            );

            List<String> list = new ArrayList<>(Arrays.stream(
                    chatCompletion
                        .replace("\n", "")
                        .split(","))
                .map(String::trim)
                .toList());

            list.removeIf(o -> userWords.stream().anyMatch(u -> u.equalsIgnoreCase(o)));

            List<GeneratedWordHistory> entities = new ArrayList<>();
            list.forEach(word -> {
                GeneratedWordHistory entity = new GeneratedWordHistory();
                entity.setUserId(currentUser.id());
                entity.setOriginal(word);
                entity.setCountryCode(currentUser.currentLang());
                entities.add(entity);
            });

            if (entities.isEmpty()) {
                this.prepareWordHistory(currentUser);
            }

            this.generatedWordHistoryService.saveAll(entities);
        }
    }

    private List<ButtonData> getButtonDataList(String word) {
        CallbackCommandEnum randomNewWordAlreadyKnow = RANDOM_NEW_WORD_ALREADY_KNOW;
        randomNewWordAlreadyKnow.setDescription(word);

        CallbackCommandEnum randomNewWordForStudy = RANDOM_NEW_WORD_FOR_STUDY;
        randomNewWordForStudy.setDescription(word);

        CallbackCommandEnum randomNewWordNotInteresting = RANDOM_NEW_WORD_NOT_INTERESTING;
        randomNewWordNotInteresting.setDescription(word);

        return List.of(
            new ButtonData("Уже знаю", randomNewWordAlreadyKnow, 1),
            new ButtonData("На вивчення", randomNewWordForStudy, 1),
            new ButtonData("Не цiкавить", randomNewWordNotInteresting, 1)
        );
    }

    private final UserService userService;
    private final TelegramUtils telegramUtils;
    private final GenerativeAiService generativeAiService;
    private final UserWordService userWordService;
    private final WordService wordService;
    private final LanguageService languageService;
    private final GeneratedWordHistoryService generatedWordHistoryService;
}
