package bot.telegram.umelon.ulingua.repository;

import bot.telegram.umelon.ulingua.model.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    Optional<Word> findByOriginal(String original);
}
