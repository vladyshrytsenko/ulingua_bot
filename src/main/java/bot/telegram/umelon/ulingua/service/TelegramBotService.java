package bot.telegram.umelon.ulingua.service;

import bot.telegram.umelon.ulingua.config.BotConfig;
import bot.telegram.umelon.ulingua.factory.CallbackHandlerFactory;
import bot.telegram.umelon.ulingua.factory.CommandHandlerFactory;
import bot.telegram.umelon.ulingua.factory.StateHandlerFactory;
import bot.telegram.umelon.ulingua.handler.CallbackHandler;
import bot.telegram.umelon.ulingua.handler.CommandHandler;
import bot.telegram.umelon.ulingua.handler.StateHandler;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.enums.MenuEnum;
import bot.telegram.umelon.ulingua.model.enums.UserState;
import bot.telegram.umelon.ulingua.utils.CountryFlagUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TelegramBotService extends TelegramLongPollingBot {

    private static final String ADD_LANG_CALLBACK = "_addlang";

    private final BotConfig config;
    private final UserService userService;
    private final LanguageService languageService;
    private final CountryFlagUtil countryFlagUtil;
    private final CommandHandlerFactory commandHandlerFactory;
    private final StateHandlerFactory stateHandlerFactory;
    private final CallbackHandlerFactory callbackHandlerFactory;

    @PostConstruct
    public void initBotCommands() {

        List<BotCommand> botCommandList = new ArrayList<>();
        botCommandList.add(new BotCommand("/profile", "профіль користувача"));
        botCommandList.add(new BotCommand("/new_word", "додати нове слово на вивчення"));

        try {
            execute(new SetMyCommands(botCommandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String firstName = update.getMessage().getChat().getFirstName();
            String lastName = update.getMessage().getChat().getLastName();
            String userName = update.getMessage().getChat().getUserName();

            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            UserDto currentUserDto = userService.getByChatId(chatId);
            UserState userState = userService.getUserState(chatId);


            MenuEnum menuEnum;
            try {
                menuEnum = MenuEnum.fromValue(messageText);
            } catch (IllegalArgumentException e) {
                menuEnum = null; // unknown command
            }

            if (menuEnum != null) {
                CommandHandler commandHandler = commandHandlerFactory.getHandler(menuEnum);
                commandHandler.handle(chatId, messageText, update);
            } else {
                if (userState != null) {
                    StateHandler stateHandler = stateHandlerFactory.getHandler(userState);
                    stateHandler.handle(chatId, messageText, currentUserDto);
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callbackData = callbackQuery.getData();

            CallbackHandler callbackHandler = callbackHandlerFactory.getHandler(callbackData);
            callbackHandler.handle(callbackQuery);
        }
    }

    @Override
    public String getBotUsername() {

        return config.getBotName();
    }

    public String getBotToken() {

        return config.getToken();
    }
}
