package bot.telegram.umelon.ulingua.handler.command;

import bot.telegram.umelon.ulingua.handler.CommandHandler;
import bot.telegram.umelon.ulingua.model.ButtonData;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum;
import bot.telegram.umelon.ulingua.service.LanguageService;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProfileHandler implements CommandHandler {

    @Override
    public void handle(long userId, String messageText, Update update, LocalMessages localMessages) {
        this.userService.setUserState(userId, null);

        UserDto currentUserDto = this.userService.getById(update.getMessage().getChatId());
        if (currentUserDto == null) {
            String message = localMessages.get("message.register_required");
            this.telegramUtils.sendMessage(userId, message, false);
        } else {
            LanguageDto nativeLang = this.languageService.getByCountryCode(currentUserDto.getNativeLang());
            LanguageDto currentLang = this.languageService.getByCountryCode(currentUserDto.getCurrentLang());
            int langTotalCount = this.languageService.getTotalCount() - 1; // except the user's native language

            List<String> list = new ArrayList<>();
            currentUserDto.getLanguages()
                .forEach(languageDto -> list.add(languageDto.getUnicode()));

            String userInfo = localMessages.get("user.info").formatted(
                currentUserDto.getCreatedAt(),
                nativeLang.getUnicode(),
                currentLang.getUnicode(),
                list
            );

            List<ButtonData> buttons = getButtonDataList(currentUserDto, localMessages, langTotalCount);

            this.telegramUtils.sendDeleteMessageRequest(userId, update.getMessage().getMessageId());
            this.telegramUtils.sendInlineKeyboard(userId, userInfo, buttons);
        }
    }

    private List<ButtonData> getButtonDataList(
        UserDto currentUserDto,
        LocalMessages localMessages,
        int langTotalCount) {

        List<ButtonData> buttons = new ArrayList<>();
        if (currentUserDto.getLanguages().size() > 1) {
            if (langTotalCount > currentUserDto.getLanguages().size()) {
                buttons.add(new ButtonData(
                    localMessages.get("button.add_language"),
                    CallbackCommandEnum.ADD_LANG,
                    1
                ));
            }
            buttons.add(new ButtonData(
                localMessages.get("button.change_current_language"),
                CallbackCommandEnum.SET_CURRENT_LANG,
                1
            ));
            buttons.add(new ButtonData(
                localMessages.get("button.remove_language"),
                CallbackCommandEnum.REMOVE_LANG,
                2
            ));
        } else {
            buttons = List.of(new ButtonData(
                localMessages.get("button.add_language"),
                CallbackCommandEnum.ADD_LANG,
                1
            ));
        }
        return buttons;
    }

    private final UserService userService;
    private final LanguageService languageService;
    private final TelegramUtils telegramUtils;
}
