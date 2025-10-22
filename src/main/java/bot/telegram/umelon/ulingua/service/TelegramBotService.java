package bot.telegram.umelon.ulingua.service;

import bot.telegram.umelon.ulingua.factory.CallbackHandlerFactory;
import bot.telegram.umelon.ulingua.factory.CommandHandlerFactory;
import bot.telegram.umelon.ulingua.factory.StateHandlerFactory;
import bot.telegram.umelon.ulingua.handler.CallbackHandler;
import bot.telegram.umelon.ulingua.handler.CommandHandler;
import bot.telegram.umelon.ulingua.handler.StateHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.LocalizationDto;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.enums.MenuEnum;
import bot.telegram.umelon.ulingua.model.enums.UserState;
import bot.telegram.umelon.ulingua.util.LocaleUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class TelegramBotService implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    @PostConstruct
    public void initBotCommands() {
        localMessages = new LocalMessages(Locale.ENGLISH);

        List<BotCommand> botCommandList = new ArrayList<>() {{
            add(new BotCommand("/profile", localMessages.get("menu.profile")));
            add(new BotCommand("/train", "Train"));
            add(new BotCommand("/language_by_country", "language_by_country"));
            add(new BotCommand("/new_word", localMessages.get("menu.new_word")));
            add(new BotCommand("/writing_sentence", "writing_sentence"));
            add(new BotCommand("/localization", localMessages.get("menu.localization")));
            add(new BotCommand("/alphabet", "Alphabet"));
            add(new BotCommand("/conversation_chat", "Free-topic communication with AI"));
        }};

        try {
            telegramClient.execute(new SetMyCommands(botCommandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            String messageText = update.getMessage().getText();
            long userId = update.getMessage().getChatId();

            UserDto currentUser = userService.getById(userId);
            UserState userState = userService.getUserState(userId);

            Locale locale;
            LocalizationDto localizationDto = localizationService.getByChatId(userId);
            if (localizationDto != null) {
                locale = LocaleUtil.getLocale(localizationDto.langCode());
            } else {
                locale = LocaleUtil.getLocale(Locale.ENGLISH.getCountry());
            }
            localMessages = new LocalMessages(locale);

            MenuEnum menuEnum;
            try {
                menuEnum = MenuEnum.fromValue(messageText);
            } catch (IllegalArgumentException e) {
                menuEnum = null; // unknown command
            }

            if (menuEnum != null) {
                CommandHandler commandHandler = commandHandlerFactory.getHandler(menuEnum);
                commandHandler.handle(userId, messageText, update, localMessages);
            } else {
                if (userState != null) {
                    StateHandler stateHandler = stateHandlerFactory.getHandler(userState);
                    stateHandler.handle(userId, messageText, currentUser, localMessages);
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callbackData = callbackQuery.getData();

            CallbackHandler callbackHandler = callbackHandlerFactory.getHandler(callbackData);
            callbackHandler.handle(callbackQuery, localMessages);
        }
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        System.out.println("Registered bot running state is: " + botSession.isRunning());
    }

    @Override
    public String getBotToken() {
        return this.token;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Value("${bot.api-key}")
    private String token;

    private TelegramClient telegramClient;

    @PostConstruct
    public void init() {
        this.telegramClient = new OkHttpTelegramClient(this.token);
    }

    private LocalMessages localMessages;

    private final UserService userService;
    private final LocalizationService localizationService;
    private final CommandHandlerFactory commandHandlerFactory;
    private final StateHandlerFactory stateHandlerFactory;
    private final CallbackHandlerFactory callbackHandlerFactory;

}
