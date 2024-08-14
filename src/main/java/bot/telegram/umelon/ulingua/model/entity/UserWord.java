package bot.telegram.umelon.ulingua.model.entity;

import bot.telegram.umelon.ulingua.model.dto.UserWordId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "user_words")
@IdClass(UserWordId.class)
@Builder
public class UserWord {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "word_id")
    private Long wordId;

    @Column(name = "usage_frequency")
    private Integer usageFrequency = 0;

}
