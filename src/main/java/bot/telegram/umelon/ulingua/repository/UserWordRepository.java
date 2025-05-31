package bot.telegram.umelon.ulingua.repository;

import bot.telegram.umelon.ulingua.model.entity.UserWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserWordRepository extends JpaRepository<UserWord, UserWord.UserWordId> {

    List<UserWord> findByUserId(Long userId);
    // Check if the user knows the word //fixme: needs to check
//    boolean existsByUserIdAndWordIdAndProgressKnewEquals(Long userId, Long wordId);
    void deleteByUserIdAndWordId(Long userId, Long wordId);
    long countByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

}
