package bot.telegram.umelon.ulingua.handler.command;

import bot.telegram.umelon.ulingua.handler.CommandHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.service.OpenAIService;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class OpenAIHandler implements CommandHandler {

    @Override
    public void handle(long chatId, String messageText, Update update, LocalMessages localMessages) {
        String response = openAIService.getChatCompletion(messageText);
        telegramUtils.sendMessage(chatId, response);
    }

    private final OpenAIService openAIService;
    private final TelegramUtils telegramUtils;
}
