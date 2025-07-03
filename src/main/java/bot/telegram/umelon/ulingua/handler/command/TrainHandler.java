package bot.telegram.umelon.ulingua.handler.command;

import bot.telegram.umelon.ulingua.handler.CommandHandler;
import bot.telegram.umelon.ulingua.model.ButtonData;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.dto.WordDto;
import bot.telegram.umelon.ulingua.model.entity.UserWord;
import bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum;
import bot.telegram.umelon.ulingua.service.LanguageService;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.service.UserWordService;
import bot.telegram.umelon.ulingua.service.WordService;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TrainHandler implements CommandHandler {

    @Override
    public void handle(long userId, String messageText, Update update, LocalMessages localMessages) {
        userService.setUserState(userId, null);

        UserDto currentUserDto = userService.getById(update.getMessage().getChatId());
        if (currentUserDto == null) {
            String message = localMessages.get("message.register_required");
            telegramUtils.sendMessage(userId, message, false);
        } else {
            Map<LanguageDto, Integer> wordCountByLanguage = new HashMap<>();

            List<UserWord> userWords = this.userWordService.findAll(userId);
            userWords.forEach(uw -> {
                Long wordId = uw.getWordId();
                WordDto word = this.wordService.getById(wordId);
                if (word != null) {
                    wordCountByLanguage.merge(word.language(), 1, Integer::sum);
                }
            });

            StringBuilder sb = new StringBuilder();
            wordCountByLanguage.forEach(
                (language, count) -> sb.append(language.unicode()).append(" ").append(count).append("\n")
            );
            LanguageDto currentLang = this.languageService.getByCountryCode(currentUserDto.currentLang());

            String trainInfo = String.format("""
                Слiв вивчено:
                %s
                Поточна мова для вивчення: %s
                Обмеження на сьогодні: %d
                """, sb, currentLang.unicode(), currentUserDto.dailyLimit()
            );
            List<ButtonData> buttons = getButtonDataList(currentUserDto, localMessages);

            telegramUtils.sendDeleteMessageRequest(userId, update.getMessage().getMessageId());
            telegramUtils.sendInlineKeyboard(userId, trainInfo, buttons);
        }
    }

    private List<ButtonData> getButtonDataList(UserDto currentUserDto, LocalMessages localMessages) {
        return List.of(
            new ButtonData("Випадкове слово", CallbackCommandEnum.RANDOM_NEW_WORD, 1),
            new ButtonData("Змiнити лiмiт", CallbackCommandEnum.CHANGE_DAILY_LIMIT, 1));
    }

    private final UserService userService;
    private final UserWordService userWordService;
    private final WordService wordService;
    private final LanguageService languageService;
    private final TelegramUtils telegramUtils;
}

