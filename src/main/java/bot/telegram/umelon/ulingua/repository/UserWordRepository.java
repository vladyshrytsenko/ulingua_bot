package bot.telegram.umelon.ulingua.repository;

import bot.telegram.umelon.ulingua.model.dto.UserWordId;
import bot.telegram.umelon.ulingua.model.entity.UserWord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWordRepository extends JpaRepository<UserWord, UserWordId> {

}
