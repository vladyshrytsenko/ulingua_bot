package bot.telegram.umelon.ulingua.handler.state;

import bot.telegram.umelon.ulingua.handler.StateHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.service.OpenAIService;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class AwaitingConversationHandler implements StateHandler {

    private final TelegramUtils telegramUtils;
    private final OpenAIService openAIService;

    @Override
    public void handle(long chatId, String messageText, UserDto currentUser, LocalMessages localMessages) {

        String chatCompletion = openAIService.getChatCompletion(format(
            "Since I am studying %s language, I want to talk to you about this topic: %s. Please, give a short answer in 2-3 sentences in %s.",
            currentUser.getCurrentLang(), messageText, currentUser.getCurrentLang()
        ));

        telegramUtils.sendMessage(chatId, chatCompletion);

    }
}


