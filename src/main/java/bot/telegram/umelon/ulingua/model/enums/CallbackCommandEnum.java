package bot.telegram.umelon.ulingua.model.enums;

import lombok.Getter;

@Getter
public enum CallbackCommandEnum {

    ADD_LANG("_add_lang"),
    REMOVE_LANG("_remove_lang"),
    ADD_NATIVE_LANG("_add_native_lang"),
    SET_CURRENT_LANG("_set_current_lang"),
    CHANGE_BOT_LANG("_change_bot_lang"),
    WRITING_SENTENCE_DISCUSS("_writing_sentence_discuss");

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
