package bot.telegram.umelon.ulingua.factory;

import bot.telegram.umelon.ulingua.handler.CallbackHandler;
import bot.telegram.umelon.ulingua.handler.callback.AddLangCallbackHandler;
import bot.telegram.umelon.ulingua.handler.callback.AddNativeLangCallbackHandler;
import bot.telegram.umelon.ulingua.handler.callback.LocalizationCallbackHandler;
import bot.telegram.umelon.ulingua.handler.callback.RemoveLangCallbackHandler;
import bot.telegram.umelon.ulingua.handler.callback.SetCurrentLangCallbackHandler;
import bot.telegram.umelon.ulingua.handler.callback.WritingSentenceDiscussCallbackHandler;
import bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CallbackHandlerFactory {

    private final ObjectFactory<AddNativeLangCallbackHandler> addNativeLangOF;
    private final ObjectFactory<AddLangCallbackHandler> addLangOF;
    private final ObjectFactory<RemoveLangCallbackHandler> removeLangOF;
    private final ObjectFactory<SetCurrentLangCallbackHandler> setCurrentLangOF;
    private final ObjectFactory<LocalizationCallbackHandler> localizationOF;
    private final ObjectFactory<WritingSentenceDiscussCallbackHandler> writingSentenceDiscussOF;

    public CallbackHandler getHandler(String callbackData) {

        if (callbackData.endsWith(CallbackCommandEnum.ADD_NATIVE_LANG.getValue())) {
            return addNativeLangOF.getObject();
        } else if (callbackData.endsWith(CallbackCommandEnum.ADD_LANG.getValue())) {
            return addLangOF.getObject();
        } else if (callbackData.endsWith(CallbackCommandEnum.REMOVE_LANG.getValue())) {
            return removeLangOF.getObject();
        } else if (callbackData.endsWith(CallbackCommandEnum.SET_CURRENT_LANG.getValue())) {
            return setCurrentLangOF.getObject();
        } else if (callbackData.endsWith(CallbackCommandEnum.CHANGE_BOT_LANG.getValue())) {
            return localizationOF.getObject();
        } else if (callbackData.endsWith(CallbackCommandEnum.WRITING_SENTENCE_DISCUSS.getValue())) {
            return writingSentenceDiscussOF.getObject();
        } else {
            throw new IllegalArgumentException("Unknown callback: " + callbackData);
        }
    }
}
