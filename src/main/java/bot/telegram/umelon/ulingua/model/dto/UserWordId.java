package bot.telegram.umelon.ulingua.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWordId implements Serializable {

    private Long userId;
    private Long wordId;

}
