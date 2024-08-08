package bot.telegram.umelon.ulingua.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Component
@PropertySource("classpath:country-flags.properties")
public class CountryFlagUtil {

    private final Map<String, String> countryMapping = new HashMap<>();

    public CountryFlagUtil(@Value("${country.flags.mapping}") String mapping) {
        parseMapping(mapping.replace("\\", ""));
    }

    private void parseMapping(String mapping) {
        String[] pairs = mapping.split(",");
        for (String pair : pairs) {
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                countryMapping.put(parts[0], parts[1]);
            }
        }
    }

    public String getFlagByCountry(String country) {
        return countryMapping.get(country);
    }
}
