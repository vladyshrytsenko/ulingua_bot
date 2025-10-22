package bot.telegram.umelon.ulingua.model.enums;

import lombok.Getter;

@Getter
public enum CallbackCommandEnum {

    ADD_LANG("_add_lang"),
    REMOVE_LANG("_remove_lang"),
    ADD_NATIVE_LANG("_add_native_lang"),
    SET_CURRENT_LANG("_set_current_lang"),
    CHANGE_BOT_LANG("_change_bot_lang"),
    WRITING_SENTENCE_DISCUSS("_writing_sentence_discuss"),
    CHANGE_DAILY_LIMIT("_change_daily_limit"),
    LIMIT_IS_FIVE("5_change_daily_limit"),
    LIMIT_IS_TEN("10_change_daily_limit"),
    LIMIT_IS_FIFTEEN("15_change_daily_limit"),
    RANDOM_NEW_WORD("_random_new_word"),
    RANDOM_NEW_WORD_ALREADY_KNOWN("_random_new_word_already_known"),
    RANDOM_NEW_WORD_TO_LEARN("_random_new_word_to_learn"),
    RANDOM_NEW_WORD_NOT_INTERESTING("_random_new_word_not_interesting"),
    LANGUAGE_PROFICIENCY_TEST("language_proficiency_test"),
    LANGUAGE_PROFICIENCY_TEST_NEXT_QUESTION("_language_proficiency_test_next_question");

    private final String value;
    private String customDescription;

    CallbackCommandEnum(String value) {
        this.value = value;
    }

    public static CallbackCommandEnum fromValue(String value) {
        for (CallbackCommandEnum callbackCommandEnum : values()) {
            if (callbackCommandEnum.value.equals(value)) {
                return callbackCommandEnum;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

    public String getDescription() {
        return customDescription == null ? null : customDescription;
    }

    public void setDescription(String customDescription) {
        this.customDescription = customDescription;
    }
}
