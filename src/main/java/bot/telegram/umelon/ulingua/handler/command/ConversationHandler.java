package bot.telegram.umelon.ulingua.handler.command;

import bot.telegram.umelon.ulingua.handler.CommandHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.enums.UserState;
import bot.telegram.umelon.ulingua.service.LanguageService;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class ConversationHandler implements CommandHandler {

    @Override
    public void handle(long userId, String messageText, Update update, LocalMessages localMessages) {
        userService.setUserState(userId, null);

        String message;
        UserDto currentUserDto = this.userService.getById(update.getMessage().getChat().getId());

        if (currentUserDto == null) {
            message = localMessages.get("message.register_required");
            telegramUtils.sendMessage(userId, message, false);
        } else {
            LanguageDto languageDto = languageService.getByCountryCode(currentUserDto.currentLang());
            message = localMessages.get("message.conversation.topic").formatted(languageDto.unicode());
            userService.setUserState(userId, UserState.AWAITING_CONVERSATION);

            telegramUtils.sendDeleteMessageRequest(userId, update.getMessage().getMessageId());
            telegramUtils.sendMessage(userId, message, true);
        }
    }

    private final UserService userService;
    private final LanguageService languageService;
    private final TelegramUtils telegramUtils;
}