package bot.telegram.umelon.ulingua.handler.command;

import bot.telegram.umelon.ulingua.handler.CommandHandler;
import bot.telegram.umelon.ulingua.model.ButtonData;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum;
import bot.telegram.umelon.ulingua.service.LanguageService;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TrainHandler implements CommandHandler {

    @Override
    public void handle(long chatId, String messageText, Update update, LocalMessages localMessages) {
        userService.setUserState(chatId, null);

        UserDto currentUserDto = userService.getByChatId(update.getMessage().getChatId());
        if (currentUserDto == null) {
            String message = localMessages.get("message.register_required");
            telegramUtils.sendMessage(chatId, message);
        } else {
            Map<LanguageDto, Integer> wordCountByLanguage = new HashMap<>();
            currentUserDto.getWords().forEach(word -> {
                wordCountByLanguage.merge(word.getLanguage(), 1, Integer::sum);
            });

            StringBuilder sb = new StringBuilder();
            wordCountByLanguage.forEach((language, count) -> {
                sb.append(language.getUnicode()).append(" - ").append(count).append("\n");
            });
            String trainInfo = String.format("""
                Слiв вивчено:
                %s
                Обмеження на сьогодні: %d
                """, sb, currentUserDto.getDailyLimit());
            List<ButtonData> buttons = getButtonDataList(currentUserDto, localMessages);
            telegramUtils.sendInlineKeyboard(chatId, trainInfo, buttons);
        }
    }

    private List<ButtonData> getButtonDataList(UserDto currentUserDto, LocalMessages localMessages) {
        return List.of(
            new ButtonData("Випадкове слово", CallbackCommandEnum.RANDOM_NEW_WORD, 1),
            new ButtonData("Змiнити лiмiт", CallbackCommandEnum.CHANGE_DAILY_LIMIT, 1));
    }

    private final UserService userService;
    private final TelegramUtils telegramUtils;
}

