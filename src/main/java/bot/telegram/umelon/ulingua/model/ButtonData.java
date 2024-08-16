package bot.telegram.umelon.ulingua.model;

import bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ButtonData {

    private String text;
    private CallbackCommandEnum callbackCommand;
    private int rowNumber;
}

