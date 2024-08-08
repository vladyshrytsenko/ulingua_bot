package bot.telegram.umelon.ulingua.factory;

import bot.telegram.umelon.ulingua.handler.StateHandler;
import bot.telegram.umelon.ulingua.handler.state.AwaitingNewWordHandler;
import bot.telegram.umelon.ulingua.model.enums.UserState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StateHandlerFactory {

    private final ObjectFactory<AwaitingNewWordHandler> awaitingNewWordHandlerObjectFactory;


    public StateHandler getHandler(UserState userState) {

        return switch (userState) {
            case AWAITING_NEW_WORD -> awaitingNewWordHandlerObjectFactory.getObject();

            default -> throw new IllegalArgumentException("Unknown state: " + userState);
        };
    }
}
