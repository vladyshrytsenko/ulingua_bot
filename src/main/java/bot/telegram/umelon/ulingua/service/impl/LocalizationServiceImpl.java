package bot.telegram.umelon.ulingua.service.impl;

import bot.telegram.umelon.ulingua.model.dto.LocalizationDto;
import bot.telegram.umelon.ulingua.model.entity.Localization;
import bot.telegram.umelon.ulingua.repository.LocalizationRepository;
import bot.telegram.umelon.ulingua.service.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocalizationServiceImpl implements LocalizationService {

    @Override
    public LocalizationDto getById(long id) {
        Localization localization = this.localizationRepository.findById(id).orElse(null);
        return localization != null ? LocalizationDto.toDto(localization) : null;
    }

    @Override
    public LocalizationDto getByChatId(long chatId) {
        Localization localization = this.localizationRepository.findByChatId(chatId).orElse(null);
        return localization != null ? LocalizationDto.toDto(localization) : null;
    }

    @Override
    public void setBotLanguage(long chatId, String langCode) {
        this.localizationRepository.findByChatId(chatId).ifPresentOrElse(
            localization -> {
                localization.setLangCode(langCode);
                this.localizationRepository.save(localization);
            }, () -> {
                Localization localization = Localization.builder()
                    .chatId(chatId)
                    .langCode(langCode)
                    .build();
                this.localizationRepository.save(localization);
            }
        );
    }

    private final LocalizationRepository localizationRepository;
}
