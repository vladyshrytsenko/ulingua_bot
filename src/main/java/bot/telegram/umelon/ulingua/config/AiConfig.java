package bot.telegram.umelon.ulingua.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean(name = "geminiChatClient")
    public ChatClient geminiChatClient(ChatClient.Builder builder) {
        return builder.build();
    }

}
