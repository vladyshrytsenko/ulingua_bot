package bot.telegram.umelon.ulingua.repository;

import bot.telegram.umelon.ulingua.model.entity.UserWord;
import bot.telegram.umelon.ulingua.model.enums.UserWordProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserWordRepository extends JpaRepository<UserWord, UserWord.UserWordId> {

    List<UserWord> findByUserId(Long userId);
    Optional<UserWord> findByUserIdAndWordId(Long userId, Long wordId);
    // Check if the user knows the word //fixme: needs to check
//    boolean existsByUserIdAndWordIdAndProgressKnewEquals(Long userId, Long wordId);
    void deleteByUserIdAndWordId(Long userId, Long wordId);
    long countByUserIdAndProgressAndCreatedAtBetween(Long userId, UserWordProgress progress, LocalDateTime start, LocalDateTime end);

}
