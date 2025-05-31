package bot.telegram.umelon.ulingua.model.entity;

import bot.telegram.umelon.ulingua.model.enums.UserWordProgress;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity(name = "user_words")
@IdClass(UserWord.UserWordId.class)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWord {

    @Id
    private Long userId;

    @Id
    private Long wordId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(value = EnumType.STRING)
    private UserWordProgress progress;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserWordId implements Serializable {

        @Column(name = "user_id")
        private Long userId;

        @Column(name = "word_id")
        private Long wordId;
    }
}
