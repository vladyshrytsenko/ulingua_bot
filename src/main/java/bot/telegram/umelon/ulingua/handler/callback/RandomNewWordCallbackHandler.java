package bot.telegram.umelon.ulingua.handler.callback;

import bot.telegram.umelon.ulingua.handler.CallbackHandler;
import bot.telegram.umelon.ulingua.model.ButtonData;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.dto.WordDto;
import bot.telegram.umelon.ulingua.model.entity.Word;
import bot.telegram.umelon.ulingua.model.enums.AiProvider;
import bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum;
import bot.telegram.umelon.ulingua.model.enums.UserWordProgress;
import bot.telegram.umelon.ulingua.model.mapper.LanguageMapper;
import bot.telegram.umelon.ulingua.model.mapper.WordMapper;
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

import java.util.List;

import static bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum.*;
import static java.lang.String.format;

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
                String chatCompletion = this.generativeAiService.chatCompletion(
                    AiProvider.GEMINI, format(
                    "I am learning %s. Give me exactly one commonly used word in this language to learn, " +
                    "with no additional context or explanation. Respond with only the word, " +
                    "and do not include a period at the end.",
                    currentUser.currentLang()
                ));

                List<ButtonData> buttons = getButtonDataList(chatCompletion);
                this.telegramUtils.sendEditMessageTextWithInlineKeyboard(
                    message.getChatId(),
                    message.getMessageId(),
                    chatCompletion,
                    buttons
                );
            }

        } else if (callbackQuery.getData().endsWith(RANDOM_NEW_WORD_ALREADY_KNOW.getValue())) {
            String wordStr = RANDOM_NEW_WORD_ALREADY_KNOW.getDescription();
            Word wordByOriginal = this.wordService.getByOriginal(RANDOM_NEW_WORD_ALREADY_KNOW.getDescription());

            if (wordByOriginal == null) {
                Word newWord = Word.builder()
                    .original(wordStr)
                    .language(LanguageMapper.MAPPER.toEntity(byCountryCode))
                    .build();
                WordDto createdWordDto = this.wordService.create(newWord);
                wordByOriginal = WordMapper.MAPPER.toEntity(createdWordDto);
            }
            this.userWordService.addWordForUser(
                message.getChatId(),
                wordByOriginal.getId(),
                UserWordProgress.KNEW
            );

            this.telegramUtils.sendDeleteMessageRequest(message.getChatId(), message.getMessageId());

            callbackQuery.setData(RANDOM_NEW_WORD.getValue());
            this.handle(callbackQuery, localMessages);

        } else if (callbackQuery.getData().endsWith(RANDOM_NEW_WORD_FOR_STUDY.getValue())) {
            String wordStr = RANDOM_NEW_WORD_FOR_STUDY.getDescription();
            Word wordByOriginal = this.wordService.getByOriginal(RANDOM_NEW_WORD_FOR_STUDY.getDescription());

            if (wordByOriginal == null) {
                Word newWord = Word.builder()
                    .original(wordStr)
                    .language(LanguageMapper.MAPPER.toEntity(byCountryCode))
                    .build();
                WordDto createdWordDto = this.wordService.create(newWord);
                wordByOriginal = WordMapper.MAPPER.toEntity(createdWordDto);
            }
            this.userWordService.addWordForUser(
                message.getChatId(),
                wordByOriginal.getId(),
                UserWordProgress.STUDYING
            );

            callbackQuery.setData(RANDOM_NEW_WORD.getValue());
            this.handle(callbackQuery, localMessages);

        } else if (callbackQuery.getData().endsWith(RANDOM_NEW_WORD_NOT_INTERESTING.getValue())) {
            String wordStr = RANDOM_NEW_WORD_NOT_INTERESTING.getDescription();
            Word wordByOriginal = this.wordService.getByOriginal(RANDOM_NEW_WORD_NOT_INTERESTING.getDescription());

            if (wordByOriginal == null) {
                Word newWord = Word.builder()
                    .original(wordStr)
                    .language(LanguageMapper.MAPPER.toEntity(byCountryCode))
                    .build();
                WordDto createdWordDto = this.wordService.create(newWord);
                wordByOriginal = WordMapper.MAPPER.toEntity(createdWordDto);
            }
            this.userWordService.addWordForUser(
                message.getChatId(),
                wordByOriginal.getId(),
                UserWordProgress.NOT_INTERESTED
            );

            callbackQuery.setData(RANDOM_NEW_WORD.getValue());
            this.handle(callbackQuery, localMessages);
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
}
