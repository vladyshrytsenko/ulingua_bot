package bot.telegram.umelon.ulingua.service;

import bot.telegram.umelon.ulingua.config.BotConfig;
import bot.telegram.umelon.ulingua.factory.CallbackHandlerFactory;
import bot.telegram.umelon.ulingua.factory.CommandHandlerFactory;
import bot.telegram.umelon.ulingua.factory.StateHandlerFactory;
import bot.telegram.umelon.ulingua.handler.CallbackHandler;
import bot.telegram.umelon.ulingua.handler.CommandHandler;
import bot.telegram.umelon.ulingua.handler.StateHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.enums.MenuEnum;
import bot.telegram.umelon.ulingua.model.enums.UserState;
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
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TelegramBotService extends TelegramLongPollingBot {

    private LocalMessages localMessages;

    private final BotConfig config;
    private final UserService userService;
    private final CommandHandlerFactory commandHandlerFactory;
    private final StateHandlerFactory stateHandlerFactory;
    private final CallbackHandlerFactory callbackHandlerFactory;

    @PostConstruct
    public void initBotCommands() {
        localMessages = new LocalMessages(Locale.ENGLISH);

        List<BotCommand> botCommandList = new ArrayList<>();
        botCommandList.add(new BotCommand("/profile", localMessages.get("menu.profile")));
        botCommandList.add(new BotCommand("/new_word", localMessages.get("menu.new_word")));
        botCommandList.add(new BotCommand("/localization", localMessages.get("menu.localization")));

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

            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            UserDto currentUser = userService.getByChatId(chatId);
            UserState userState = userService.getUserState(chatId);

            Locale userLocale = getUserLocale(currentUser);
            localMessages = new LocalMessages(userLocale);

            MenuEnum menuEnum;
            try {
                menuEnum = MenuEnum.fromValue(messageText);
            } catch (IllegalArgumentException e) {
                menuEnum = null; // unknown command
            }

            if (menuEnum != null) {
                CommandHandler commandHandler = commandHandlerFactory.getHandler(menuEnum);
                commandHandler.handle(chatId, messageText, update, localMessages);
            } else {
                if (userState != null) {
                    StateHandler stateHandler = stateHandlerFactory.getHandler(userState);
                    stateHandler.handle(chatId, messageText, currentUser, localMessages);
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callbackData = callbackQuery.getData();

            CallbackHandler callbackHandler = callbackHandlerFactory.getHandler(callbackData);
            callbackHandler.handle(callbackQuery, localMessages);
        }
    }

    @Override
    public String getBotUsername() {

        return config.getBotName();
    }

    public String getBotToken() {

        return config.getToken();
    }

    public Locale getUserLocale(UserDto currentUser) {
        if (currentUser != null) {
            switch (currentUser.getLocalization()) {
                case "AE":
                    return new Locale("ar");
                case "CN":
                    return new Locale("zh", "CN");
                case "DE":
                    return Locale.GERMAN;
                case "FR":
                    return Locale.FRENCH;
                case "ES":
                    return new Locale("es");
                case "IT":
                    return Locale.ITALIAN;
                case "JP":
                    return new Locale("ja", "JP");
                case "KR":
                    return new Locale("ko", "KR");
                case "PL":
                    return new Locale("pl");
                case "PT":
                    return new Locale("pt");
                case "RU":
                    return Locale.forLanguageTag("ru-RU");
                case "TR":
                    return new Locale("tr");
                case "UA":
                    return new Locale("uk", "UA");
            }
        }
        return Locale.ENGLISH;
    }
}
