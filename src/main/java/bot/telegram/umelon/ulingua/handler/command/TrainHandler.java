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
public class TrainHandler implements CommandHandler {

    private final UserService userService;
    private final LanguageService languageService;
    private final TelegramUtils telegramUtils;

    @Override
    public void handle(long chatId, String messageText, Update update, LocalMessages localMessages) {
        userService.setUserState(chatId, null);

        UserDto currentUserDto = userService.getByChatId(update.getMessage().getChatId());
        if (currentUserDto == null) {
            String message = localMessages.get("message.register_required");
            telegramUtils.sendMessage(chatId, message);
        } else {
            LanguageDto nativeLang = languageService.getByCountryCode(currentUserDto.getNativeLang());
            LanguageDto currentLang = languageService.getByCountryCode(currentUserDto.getCurrentLang());
            List<String> list = new ArrayList<>();
            currentUserDto.getLanguages().forEach(languageDto -> list.add(languageDto.getUnicode()));

//            String userInfo = String.format(localMessages.get("user.info"), currentUserDto.getCreatedAt(), nativeLang.getUnicode(), currentLang.getUnicode(), list);
            String trainInfo = "Слiв вивчено: " + currentUserDto.getWords().size();
            List<ButtonData> buttons = getButtonDataList(currentUserDto, localMessages);
            telegramUtils.sendInlineKeyboard(chatId, trainInfo, buttons);
        }
    }

    private List<ButtonData> getButtonDataList(UserDto currentUserDto, LocalMessages localMessages) {
        List<ButtonData> buttons;
        if (currentUserDto.getLanguages().size() > 1) {
            buttons = List.of(
                new ButtonData(localMessages.get("button.add_language"), CallbackCommandEnum.ADD_LANG, 1),
                new ButtonData(localMessages.get("button.change_current_language"), CallbackCommandEnum.SET_CURRENT_LANG, 1),
                new ButtonData(localMessages.get("button.remove_language"), CallbackCommandEnum.REMOVE_LANG, 2)
            );
        } else {
            buttons = List.of(
                new ButtonData(localMessages.get("button.add_language"), CallbackCommandEnum.ADD_LANG, 1)
            );
        }
        return buttons;
    }
}

