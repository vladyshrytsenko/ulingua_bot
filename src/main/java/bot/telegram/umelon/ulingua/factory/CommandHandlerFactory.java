package bot.telegram.umelon.ulingua.factory;

import bot.telegram.umelon.ulingua.handler.CommandHandler;
import bot.telegram.umelon.ulingua.handler.command.AlphabetHandler;
import bot.telegram.umelon.ulingua.handler.command.CancelHandler;
import bot.telegram.umelon.ulingua.handler.command.ConversationHandler;
import bot.telegram.umelon.ulingua.handler.command.LocalizationHandler;
import bot.telegram.umelon.ulingua.handler.command.NewWordHandler;
import bot.telegram.umelon.ulingua.handler.command.OpenAIHandler;
import bot.telegram.umelon.ulingua.handler.command.ProfileHandler;
import bot.telegram.umelon.ulingua.handler.command.RegisterHandler;
import bot.telegram.umelon.ulingua.handler.command.WritingSentenceHandler;
import bot.telegram.umelon.ulingua.model.enums.MenuEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandHandlerFactory {

    private final ObjectFactory<RegisterHandler> registerOF;
    private final ObjectFactory<ProfileHandler> profileOF;
    private final ObjectFactory<NewWordHandler> newWordOF;
    private final ObjectFactory<LocalizationHandler> localizationOF;
    private final ObjectFactory<AlphabetHandler> alphabetOF;
    private final ObjectFactory<ConversationHandler> conversationOF;
    private final ObjectFactory<WritingSentenceHandler> writingSentenceOF;
    private final ObjectFactory<CancelHandler> cancelOF;

    private final ObjectFactory<OpenAIHandler> openAIOF;

    public CommandHandler getHandler(MenuEnum menuEnum) {

        return switch (menuEnum) {
            case REGISTER -> registerOF.getObject();
            case PROFILE -> profileOF.getObject();
            case NEW_WORD -> newWordOF.getObject();
            case LOCALIZATION -> localizationOF.getObject();
            case ALPHABET -> alphabetOF.getObject();
            case CONVERSATION_CHAT -> conversationOF.getObject();
            case WRITING_SENTENCE -> writingSentenceOF.getObject();
            case CANCEL -> cancelOF.getObject();

            default -> openAIOF.getObject();
        };
    }
}
