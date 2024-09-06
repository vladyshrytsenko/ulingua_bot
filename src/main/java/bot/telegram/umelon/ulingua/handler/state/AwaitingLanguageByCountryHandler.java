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
public class AwaitingLanguageByCountryHandler implements StateHandler {

    private final TelegramUtils telegramUtils;
    private final OpenAIService openAIService;

    @Override
    public void handle(long chatId, String messageText, UserDto currentUser, LocalMessages localMessages) {

        String chatCompletion = openAIService.getChatCompletion(format(
            "what is the language of communication in %s? Answer in one word and in %s. " +
            "Or if there are several, then answer separated by commas. " +
            "Or if such a country does not exist, then say so",
            messageText, currentUser.getLocalization()
        ));

        telegramUtils.sendMessage(chatId, chatCompletion);

    }
}
