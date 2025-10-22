package bot.telegram.umelon.ulingua.handler.command;

import bot.telegram.umelon.ulingua.handler.CommandHandler;
import bot.telegram.umelon.ulingua.model.ButtonData;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.dto.LocalizationDto;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum;
import bot.telegram.umelon.ulingua.service.LanguageService;
import bot.telegram.umelon.ulingua.service.LocalizationService;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.util.LocaleUtil;
import bot.telegram.umelon.ulingua.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProfileHandler implements CommandHandler {

    @Override
    public void handle(long userId, String messageText, Update update, LocalMessages localMessages) {
        userService.setUserState(userId, null);

        UserDto currentUserDto = userService.getById(update.getMessage().getChatId());
        if (currentUserDto == null) {
            String message = localMessages.get("message.register_required");
            telegramUtil.sendMessage(userId, message, false);
        } else {
            LanguageDto nativeLang = languageService.getByCountryCode(currentUserDto.nativeLang());
            LanguageDto currentLang = languageService.getByCountryCode(currentUserDto.currentLang());
            int langTotalCount = languageService.getTotalCount() - 1; // except the user's native language

            List<String> list = new ArrayList<>();
            currentUserDto.languages()
                .forEach(languageDto -> list.add(languageDto.unicode()));

            LocalizationDto currentLocalization = localizationService.getByChatId(currentUserDto.id());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy, HH:mm", LocaleUtil.getLocale(currentLocalization.langCode()));
            String registrationDate = currentUserDto.createdAt().format(formatter);

            String userInfo = localMessages.get("user.info")
                .formatted(registrationDate, nativeLang.unicode(), currentLang.unicode(), list);

            List<ButtonData> buttons = this.getButtonDataList(currentUserDto, localMessages, langTotalCount);

            telegramUtil.sendDeleteMessageRequest(userId, update.getMessage().getMessageId());
            telegramUtil.sendInlineKeyboard(userId, userInfo, buttons);
        }
    }

    private List<ButtonData> getButtonDataList(UserDto currentUserDto, LocalMessages localMessages, int langTotalCount) {
        List<ButtonData> buttons = new ArrayList<>();
        if (currentUserDto.languages().size() > 1) {
            if (langTotalCount > currentUserDto.languages().size()) {
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
    private final LocalizationService localizationService;
    private final TelegramUtil telegramUtil;
}
