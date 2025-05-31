package bot.telegram.umelon.ulingua.factory;

import bot.telegram.umelon.ulingua.handler.CallbackHandler;
import bot.telegram.umelon.ulingua.handler.callback.AddLangCallbackHandler;
import bot.telegram.umelon.ulingua.handler.callback.AddNativeLangCallbackHandler;
import bot.telegram.umelon.ulingua.handler.callback.DailyLimitCallbackHandler;
import bot.telegram.umelon.ulingua.handler.callback.LocalizationCallbackHandler;
import bot.telegram.umelon.ulingua.handler.callback.RandomNewWordCallbackHandler;
import bot.telegram.umelon.ulingua.handler.callback.RemoveLangCallbackHandler;
import bot.telegram.umelon.ulingua.handler.callback.SetCurrentLangCallbackHandler;
import bot.telegram.umelon.ulingua.handler.callback.WritingSentenceDiscussCallbackHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

import static bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum.*;

@Component
@RequiredArgsConstructor
public class CallbackHandlerFactory {

    public CallbackHandler getHandler(String callbackData) {
        if (callbackData.endsWith(ADD_NATIVE_LANG.getValue())) {
            return addNativeLangOF.getObject();

        } else if (callbackData.endsWith(ADD_LANG.getValue())) {
            return addLangOF.getObject();

        } else if (callbackData.endsWith(REMOVE_LANG.getValue())) {
            return removeLangOF.getObject();

        } else if (callbackData.endsWith(SET_CURRENT_LANG.getValue())) {
            return setCurrentLangOF.getObject();

        } else if (callbackData.endsWith(CHANGE_BOT_LANG.getValue())) {
            return localizationOF.getObject();

        } else if (callbackData.endsWith(WRITING_SENTENCE_DISCUSS.getValue())) {
            return writingSentenceDiscussOF.getObject();

        } else if (callbackData.endsWith(RANDOM_NEW_WORD.getValue())
                   || callbackData.endsWith(RANDOM_NEW_WORD_ALREADY_KNOW.getValue())
                   || callbackData.endsWith(RANDOM_NEW_WORD_FOR_STUDY.getValue())
                   || callbackData.endsWith(RANDOM_NEW_WORD_NOT_INTERESTING.getValue())) {
            return randomNewWordOF.getObject();

        } else if (callbackData.endsWith(CHANGE_DAILY_LIMIT.getValue())) {
            return dailyLimitOF.getObject();

        } else {
            throw new IllegalArgumentException("Unknown callback: " + callbackData);
        }
    }

    private final ObjectFactory<AddNativeLangCallbackHandler> addNativeLangOF;
    private final ObjectFactory<AddLangCallbackHandler> addLangOF;
    private final ObjectFactory<RemoveLangCallbackHandler> removeLangOF;
    private final ObjectFactory<SetCurrentLangCallbackHandler> setCurrentLangOF;
    private final ObjectFactory<LocalizationCallbackHandler> localizationOF;
    private final ObjectFactory<WritingSentenceDiscussCallbackHandler> writingSentenceDiscussOF;
    private final ObjectFactory<RandomNewWordCallbackHandler> randomNewWordOF;
    private final ObjectFactory<DailyLimitCallbackHandler> dailyLimitOF;
}
