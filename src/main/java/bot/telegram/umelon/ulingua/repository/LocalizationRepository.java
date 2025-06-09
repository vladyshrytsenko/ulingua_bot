package bot.telegram.umelon.ulingua.repository;

import bot.telegram.umelon.ulingua.model.entity.Localization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocalizationRepository extends JpaRepository<Localization, Long> {

    Optional<Localization> findByChatId(Long chatId);
}
