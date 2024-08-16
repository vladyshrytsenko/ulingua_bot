package bot.telegram.umelon.ulingua.model.enums;

import lombok.Getter;

@Getter
public enum MenuEnum {

    REGISTER("/register"),
    PROFILE("/profile"),
    ADD_LANG("/add_lang"),
    NEW_WORD("/new_word"),
    CUSTOM("привет");

    private final String value;

    MenuEnum(String value) {
        this.value = value;
    }

    public static MenuEnum fromValue(String value) {
        for (MenuEnum menuEnum : values()) {
            if (menuEnum.value.equals(value)) {
                return menuEnum;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
