package bot.telegram.umelon.ulingua.repository;

import bot.telegram.umelon.ulingua.model.entity.redis.GeneratedWordHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeneratedWordHistoryRepository extends CrudRepository<GeneratedWordHistory, String> {

    List<GeneratedWordHistory> findAllByCountryCode(String countryCode);
    Optional<GeneratedWordHistory> findFirstByCountryCode(String countryCode);
    Optional<GeneratedWordHistory> findByOriginal(String original);
}
