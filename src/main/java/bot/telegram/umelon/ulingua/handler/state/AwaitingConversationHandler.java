package bot.telegram.umelon.ulingua.handler.state;

import bot.telegram.umelon.ulingua.handler.StateHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.enums.AiProvider;
import bot.telegram.umelon.ulingua.service.GenerativeAiService;
import bot.telegram.umelon.ulingua.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class AwaitingConversationHandler implements StateHandler {

    @Override
    public void handle(long userId, String messageText, UserDto currentUser, LocalMessages localMessages) {
        String chatCompletion = generativeAiService.chatCompletion(
            AiProvider.GEMINI, format(
            "Since I am studying %s language, I want to talk to you about this topic: %s. " +
            "Please, give a short answer in 2-3 sentences in %s.",
            currentUser.currentLang(), messageText, currentUser.currentLang()
        ));

        telegramUtil.sendMessage(userId, chatCompletion, true);
    }

    private final TelegramUtil telegramUtil;
    private final GenerativeAiService generativeAiService;
}


