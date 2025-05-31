package bot.telegram.umelon.ulingua.handler.callback;

import bot.telegram.umelon.ulingua.handler.CallbackHandler;
import bot.telegram.umelon.ulingua.model.ButtonData;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

import static bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum.LIMIT_IS_FIFTEEN;
import static bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum.LIMIT_IS_FIVE;
import static bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum.LIMIT_IS_TEN;

@Component
@RequiredArgsConstructor
public class DailyLimitCallbackHandler implements CallbackHandler {

    @Override
    public void handle(CallbackQuery callbackQuery, LocalMessages localMessages) {
        String callbackData = callbackQuery.getData();
        Long callbackChatId = callbackQuery.getMessage().getChatId();
        Integer callbackMessageId = callbackQuery.getMessage().getMessageId();

        if (callbackData.equals(CallbackCommandEnum.CHANGE_DAILY_LIMIT.getValue())) {
            List<ButtonData> buttons = this.getButtonDataList();

            this.telegramUtils.sendEditMessageTextWithInlineKeyboard(
                callbackChatId,
                callbackMessageId,
                "Який лiмiт встановити?",
                buttons
            );

        } else if (callbackData.equals(LIMIT_IS_FIVE.getValue())) {
            this.userService.setDailyLimit(callbackChatId, (byte) 5);
        } else if (callbackData.equals(LIMIT_IS_TEN.getValue())) {
            this.userService.setDailyLimit(callbackChatId, (byte) 10);
        } else if (callbackData.equals(LIMIT_IS_FIFTEEN.getValue())) {
            this.userService.setDailyLimit(callbackChatId, (byte) 15);
        }
    }

    private List<ButtonData> getButtonDataList() {
        return List.of(
            new ButtonData("5", LIMIT_IS_FIVE, 1),
            new ButtonData("10", LIMIT_IS_TEN, 1),
            new ButtonData("15", LIMIT_IS_FIFTEEN, 1)
        );
    }

    private final UserService userService;
    private final TelegramUtils telegramUtils;
}

