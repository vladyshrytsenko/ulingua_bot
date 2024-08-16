//package bot.telegram.umelon.ulingua.handler.command;
//
//import bot.telegram.umelon.ulingua.handler.CommandHandler;
//import bot.telegram.umelon.ulingua.model.dto.UserDto;
//import bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum;
//import bot.telegram.umelon.ulingua.service.UserService;
//import bot.telegram.umelon.ulingua.utils.TelegramUtils;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.meta.api.objects.Update;
//
//@Component
//@RequiredArgsConstructor
//public class AddLangHandler implements CommandHandler {
//
//    private final UserService userService;
//    private final TelegramUtils telegramUtils;
//
//    @Override
//    public void handle(long chatId, String messageText, Update update) {
//
//        String message;
//
//        UserDto currentUserDto = this.userService.getByChatId(update.getMessage().getChat().getId());
//
//        if (currentUserDto == null) {
//            message = "Ви ще не зареєстровані в боті. Натисніть /register для початку роботи.";
//            telegramUtils.sendMessage(chatId, message);
//        } else {
//            telegramUtils.sendLanguagesInlineKeyboard(chatId, "Яку мову додати?", CallbackCommandEnum.ADD_LANG);
//        }
//    }
//}
