package bot.telegram.umelon.ulingua.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LanguageDataLoader implements CommandLineRunner {

    @Override
    public void run(String... args) {
        languageService.initializeLanguages();
    }

    private final LanguageService languageService;
}
