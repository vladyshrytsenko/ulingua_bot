package bot.telegram.umelon.ulingua.factory;

import bot.telegram.umelon.ulingua.handler.CommandHandler;
import bot.telegram.umelon.ulingua.handler.command.NewWordHandler;
import bot.telegram.umelon.ulingua.handler.command.OpenAIHandler;
import bot.telegram.umelon.ulingua.handler.command.ProfileHandler;
import bot.telegram.umelon.ulingua.handler.command.RegisterHandler;
import bot.telegram.umelon.ulingua.model.enums.MenuEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandHandlerFactory {

    private final ObjectFactory<RegisterHandler> registerHandlerObjectFactory;
    private final ObjectFactory<ProfileHandler> profileHandlerObjectFactory;
//    private final ObjectFactory<AddLangHandler> addLangHandlerObjectFactory;
    private final ObjectFactory<NewWordHandler> newWordHandlerObjectFactory;

    private final ObjectFactory<OpenAIHandler> openAIHandlerObjectFactory;

    public CommandHandler getHandler(MenuEnum menuEnum) {

        return switch (menuEnum) {
            case REGISTER -> registerHandlerObjectFactory.getObject();
            case PROFILE -> profileHandlerObjectFactory.getObject();
//            case ADD_LANG -> addLangHandlerObjectFactory.getObject();
            case NEW_WORD -> newWordHandlerObjectFactory.getObject();

            //            default -> throw new IllegalArgumentException("Unknown command: " + menuEnum);
            default -> openAIHandlerObjectFactory.getObject();
        };
    }
}
