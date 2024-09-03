package bot.telegram.umelon.ulingua.handler.callback;

import bot.telegram.umelon.ulingua.handler.CallbackHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.enums.UserState;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@RequiredArgsConstructor
public class WritingSentenceDiscussCallbackHandler implements CallbackHandler {

    private final UserService userService;
    private final TelegramUtils telegramUtils;

    @Override
    public void handle(CallbackQuery callbackQuery, LocalMessages localMessages) {
        Long callbackChatId = callbackQuery.getMessage().getChatId();

        telegramUtils.sendMessage(callbackChatId, localMessages.get("message.conversation.add_comment"));
        userService.setUserState(callbackChatId, UserState.AWAITING_SENTENCE_DISCUSS);
    }
}
