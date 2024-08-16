package bot.telegram.umelon.ulingua.model.enums;

import lombok.Getter;

@Getter
public enum CallbackCommandEnum {

    ADD_LANG("_add_lang"),
    REMOVE_LANG("_remove_lang"),
    ADD_NATIVE_LANG("_add_native_lang"),
    SET_CURRENT_LANG("_set_current_lang");

    private final String value;

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
}
