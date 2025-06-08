package bot.telegram.umelon.ulingua.utils;

import lombok.NoArgsConstructor;

import java.util.Locale;

@NoArgsConstructor
public class LocaleUtils {

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
                case "PL":
                    return new Locale("pl");
                case "PT":
                    return new Locale("pt");
                case "TR":
                    return new Locale("tr");
                case "UA":
                    return new Locale("uk", "UA");
            }
        }
        return Locale.ENGLISH;
    }
}
