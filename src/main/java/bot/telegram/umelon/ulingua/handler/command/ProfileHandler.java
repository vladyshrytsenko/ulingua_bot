package bot.telegram.umelon.ulingua.handler.command;

import bot.telegram.umelon.ulingua.handler.CommandHandler;
import bot.telegram.umelon.ulingua.model.ButtonData;
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

    private final UserService userService;
    private final LanguageService languageService;
    private final TelegramUtils telegramUtils;

    @Override
    public void handle(long chatId, String messageText, Update update) {

        UserDto currentUserDto = userService.getByChatId(update.getMessage().getChatId());
        if (currentUserDto == null) {
            String message = "Ви ще не зареєстровані в боті. Натисніть /register для початку роботи.";
            telegramUtils.sendMessage(chatId, message);
        } else {
            LanguageDto nativeLang = languageService.getByCountryCode(currentUserDto.getNativeLang());
            LanguageDto currentLang = languageService.getByCountryCode(currentUserDto.getCurrentLang());
            List<String> list = new ArrayList<>();
            currentUserDto.getLanguages().forEach(languageDto -> list.add(languageDto.getUnicode()));

            String userInfo = String.format("""
                Дата реєстрації: %s
                Рідна мова: %s
                Поточна мова для вивчення: %s

                Список мов на вивченні: %s
                """, currentUserDto.getCreatedAt(), nativeLang.getUnicode(), currentLang.getUnicode(), list);

            if (currentUserDto.getLanguages().size() > 1) {
                List<ButtonData> buttons = List.of(
                    new ButtonData("Змінити мову", CallbackCommandEnum.SET_CURRENT_LANG, 1),
                    new ButtonData("Видалити мову", CallbackCommandEnum.REMOVE_LANG, 1)
                );
                telegramUtils.sendInlineKeyboard(chatId, userInfo, buttons);
            } else {
                telegramUtils.sendMessage(chatId, userInfo);
            }
        }
    }
}
