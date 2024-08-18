package bot.telegram.umelon.ulingua.model;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocalMessages {
    private final ResourceBundle messages;

    public LocalMessages(Locale locale) {
        this.messages = ResourceBundle.getBundle("localization/messages", locale);
    }

    public String get(String key) {
        return messages.getString(key);
    }
}
