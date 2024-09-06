package bot.telegram.umelon.ulingua.factory;

import bot.telegram.umelon.ulingua.handler.StateHandler;
import bot.telegram.umelon.ulingua.handler.state.AwaitingConversationHandler;
import bot.telegram.umelon.ulingua.handler.state.AwaitingLanguageByCountryHandler;
import bot.telegram.umelon.ulingua.handler.state.AwaitingNewWordHandler;
import bot.telegram.umelon.ulingua.handler.state.AwaitingSentenceDiscussHandler;
import bot.telegram.umelon.ulingua.handler.state.AwaitingSentenceHandler;
import bot.telegram.umelon.ulingua.model.enums.UserState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StateHandlerFactory {

    private final ObjectFactory<AwaitingNewWordHandler> awaitingNewWordOF;
    private final ObjectFactory<AwaitingConversationHandler> awaitingConversationOF;
    private final ObjectFactory<AwaitingSentenceHandler> awaitingSentenceOF;
    private final ObjectFactory<AwaitingSentenceDiscussHandler> awaitingSentenceDiscussOF;
    private final ObjectFactory<AwaitingLanguageByCountryHandler> awaitingLanguageByCountryOF;

    public StateHandler getHandler(UserState userState) {

        return switch (userState) {
            case AWAITING_NEW_WORD -> awaitingNewWordOF.getObject();
            case AWAITING_CONVERSATION -> awaitingConversationOF.getObject();
            case AWAITING_SENTENCE -> awaitingSentenceOF.getObject();
            case AWAITING_SENTENCE_DISCUSS -> awaitingSentenceDiscussOF.getObject();
            case AWAITING_COUNTRY -> awaitingLanguageByCountryOF.getObject();

            default -> throw new IllegalArgumentException("Unknown state: " + userState);
        };
    }
}
