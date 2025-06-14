package bot.telegram.umelon.ulingua.handler.state;

import bot.telegram.umelon.ulingua.handler.StateHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.enums.AiProvider;
import bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum;
import bot.telegram.umelon.ulingua.service.GenerativeAiService;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class AwaitingSentenceDiscussHandler implements StateHandler {

    @Override
    public void handle(long userId, String messageText, UserDto currentUser, LocalMessages localMessages) {

        String previousResponse = CallbackCommandEnum.WRITING_SENTENCE_DISCUSS.getDescription();
        if (previousResponse != null) {
            String discussionPrompt = generativeAiService.chatCompletion(
                AiProvider.GEMINI, format(
                "I'd like to discuss the following response you provided: '%s'. " +
                "And that is my next question: %s. Please, answer in %s language?",
                previousResponse, messageText, currentUser.nativeLang()
            ));

            telegramUtils.sendMessage(userId, discussionPrompt, true);
        }
    }

    private final TelegramUtils telegramUtils;
    private final GenerativeAiService generativeAiService;
}
