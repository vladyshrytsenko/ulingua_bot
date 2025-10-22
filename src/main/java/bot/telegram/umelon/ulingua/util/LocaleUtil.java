package bot.telegram.umelon.ulingua.util;

import lombok.NoArgsConstructor;

import java.util.Locale;

@NoArgsConstructor
public class LocaleUtil {

    public static Locale getLocale(String localization) {
        if (localization != null) {
            switch (localization) {
                case "DE":
                    return Locale.GERMAN;
                case "FR":
                    return Locale.FRENCH;
                case "ES":
                    return new Locale("es");
                case "IT":
                    return Locale.ITALIAN;
                case "JP":
                    return new Locale("ja", "JP");
                case "UA":
                    return new Locale("uk", "UA");
            }
        }
        return Locale.ENGLISH;
    }
}
