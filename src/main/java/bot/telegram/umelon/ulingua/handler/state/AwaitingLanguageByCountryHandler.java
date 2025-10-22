package bot.telegram.umelon.ulingua.handler.state;

import bot.telegram.umelon.ulingua.handler.StateHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.LocalizationDto;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.enums.AiProvider;
import bot.telegram.umelon.ulingua.service.GenerativeAiService;
import bot.telegram.umelon.ulingua.service.LocalizationService;
import bot.telegram.umelon.ulingua.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AwaitingLanguageByCountryHandler implements StateHandler {

    @Override
    public void handle(long userId, String messageText, UserDto currentUser, LocalMessages localMessages) {
        LocalizationDto localizationDto = localizationService.getByChatId(userId);

        String chatCompletion = generativeAiService.chatCompletion(
            AiProvider.GEMINI,
            "what is the language of communication in %s? Answer in one word and in %s. ".formatted(messageText, localizationDto.langCode()) +
            "Or if there are several, then answer separated by commas. Or if such a country does not exist, then say so"
        );

        telegramUtil.sendMessage(userId, chatCompletion, false);
    }

    private final TelegramUtil telegramUtil;
    private final GenerativeAiService generativeAiService;
    private final LocalizationService localizationService;
}
