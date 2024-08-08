package bot.telegram.umelon.ulingua.handler.state;

import bot.telegram.umelon.ulingua.handler.StateHandler;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.service.OpenAIService;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AwaitingNewWordHandler implements StateHandler {

    private final UserService userService;
    private final TelegramUtils telegramUtils;
    private final OpenAIService openAIService;

    @Override
    public void handle(long chatId, String messageText, UserDto currentUserDto) {

        String word = messageText.split(" ")[0];
        String chatCompletion = openAIService.getChatCompletion(String.format(
            "существует ли слово '%s' в %s языке? ответь только: да или нет, и на русском",
            word,
            currentUserDto.getCurrentLang()
        ));

        telegramUtils.sendMessage(chatId, chatCompletion);
        if (chatCompletion.toLowerCase().contains("да")) {
            telegramUtils.sendMessage(chatId, String.format("Записую '%s' до списку на вивчення.", messageText));
        }

//        wordService.saveNewWordForUser(chatId, messageText);
        userService.setUserState(chatId, null);
    }
}

