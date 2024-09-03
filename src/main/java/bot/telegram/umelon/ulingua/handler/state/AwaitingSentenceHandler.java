package bot.telegram.umelon.ulingua.handler.state;

import bot.telegram.umelon.ulingua.handler.StateHandler;
import bot.telegram.umelon.ulingua.model.ButtonData;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum;
import bot.telegram.umelon.ulingua.service.OpenAIService;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class AwaitingSentenceHandler implements StateHandler {

    private final TelegramUtils telegramUtils;
    private final OpenAIService openAIService;

    @Override
    public void handle(long chatId, String messageText, UserDto currentUser, LocalMessages localMessages) {

        String chatCompletion = openAIService.getChatCompletion(format(
            "Check the sentence '%s' written in %s. If there are any errors, please explain them in %s, keeping the original words with mistakes in %s.",
            messageText, currentUser.getCurrentLang(), currentUser.getNativeLang(), currentUser.getCurrentLang()
        ));

        CallbackCommandEnum sentenceDiscuss = CallbackCommandEnum.WRITING_SENTENCE_DISCUSS;
        sentenceDiscuss.setDescription(chatCompletion);
        List<ButtonData> buttons = List.of(
                new ButtonData(localMessages.get("button.discuss"), sentenceDiscuss, 1)
            );
        telegramUtils.sendInlineKeyboard(chatId, chatCompletion, buttons);

    }
}
