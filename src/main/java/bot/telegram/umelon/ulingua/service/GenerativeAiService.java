package bot.telegram.umelon.ulingua.service;

import bot.telegram.umelon.ulingua.model.enums.AiProvider;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GenerativeAiService {

    public String chatCompletion(AiProvider provider, String prompt) {
        return this.chatClients.get(provider).prompt(prompt)
            .call()
            .content();
    }


    public GenerativeAiService(
        @Qualifier("geminiChatClient") ChatClient geminiClient
    ) {
        chatClients.put(AiProvider.GEMINI, geminiClient);
    }

    private final Map<AiProvider, ChatClient> chatClients = new ConcurrentHashMap<>();
}
