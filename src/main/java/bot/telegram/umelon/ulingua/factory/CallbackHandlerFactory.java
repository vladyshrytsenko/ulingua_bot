package bot.telegram.umelon.ulingua.factory;

import bot.telegram.umelon.ulingua.handler.CallbackHandler;
import bot.telegram.umelon.ulingua.handler.callback.AddLangCallbackHandler;
import bot.telegram.umelon.ulingua.handler.callback.AddNativeLangCallbackHandler;
import bot.telegram.umelon.ulingua.handler.callback.SetCurrentLangCallbackHandler;
import bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CallbackHandlerFactory {

    private final ObjectFactory<AddNativeLangCallbackHandler> addNativeLangHandlerObjectFactory;
    private final ObjectFactory<AddLangCallbackHandler> addLangHandlerObjectFactory;
    private final ObjectFactory<SetCurrentLangCallbackHandler> setCurrentLangHandlerObjectFactory;

    public CallbackHandler getHandler(String callbackData) {

        if (callbackData.endsWith(CallbackCommandEnum.ADD_NATIVE_LANG.getValue())) {
            return addNativeLangHandlerObjectFactory.getObject();
        } else if (callbackData.endsWith(CallbackCommandEnum.ADD_LANG.getValue())) {
            return addLangHandlerObjectFactory.getObject();
        } else if (callbackData.endsWith(CallbackCommandEnum.SET_CURRENT_LANG.getValue())) {
            return setCurrentLangHandlerObjectFactory.getObject();
        } else {
            throw new IllegalArgumentException("Unknown callback: " + callbackData);
        }
    }
}
