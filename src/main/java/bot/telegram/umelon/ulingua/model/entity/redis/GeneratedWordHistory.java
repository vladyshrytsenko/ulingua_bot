package bot.telegram.umelon.ulingua.model.entity.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("GeneratedWordHistory")
public class GeneratedWordHistory {

    @Id
    private String id;

    private Long userId;

    @Indexed
    private String countryCode;

    @Indexed
    private String original;
}
