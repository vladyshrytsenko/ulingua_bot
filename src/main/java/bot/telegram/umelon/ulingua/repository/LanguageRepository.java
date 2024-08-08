package bot.telegram.umelon.ulingua.repository;

import bot.telegram.umelon.ulingua.model.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {

    Optional<Language> getByCountryCode(String code);

}
