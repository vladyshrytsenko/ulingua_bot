package bot.telegram.umelon.ulingua.handler.callback;

import bot.telegram.umelon.ulingua.handler.CallbackHandler;
import bot.telegram.umelon.ulingua.model.LocalMessages;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.enums.AiProvider;
import bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum;
import bot.telegram.umelon.ulingua.service.GenerativeAiService;
import bot.telegram.umelon.ulingua.service.UserService;
import bot.telegram.umelon.ulingua.util.TelegramUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.ArrayList;
import java.util.List;

import static bot.telegram.umelon.ulingua.model.enums.CallbackCommandEnum.*;

@Component
@RequiredArgsConstructor
public class LanguageQuizCallbackHandler implements CallbackHandler {

    private List<LanguageQuizResponseDto> languageQuizResponseDtoList = new ArrayList<>();
    private List<UserAnswer> userAnswers = new ArrayList<>();
    private int questionsCount = 0;
    private int correctAnswersCount = 0;

    @Override
    public void handle(CallbackQuery callbackQuery, LocalMessages localMessages) {
        MaybeInaccessibleMessage message = callbackQuery.getMessage();

        UserDto currentUser = userService.getById(message.getChatId());

        if (callbackQuery.getData().endsWith(LANGUAGE_PROFICIENCY_TEST.getValue())) {
            String promptTemplate = String.format(
                """
                    You are a language proficiency test generator. Your task is to create a multiple-choice quiz in the specified 
                    language to assess a user's skill level. The quiz should start with beginner-level questions and gradually increase in difficulty.
                    
                    TASK:
                    1. The quiz MUST have %d questions.
                    2. For each question, provide 4 multiple-choice options. Only one option should be correct.
                    3. The questions MUST cover various aspects of the language, including grammar, vocabulary, and sentence structure.
                    4. The questions should progress from A1/A2 (beginner) to B1/B2 (intermediate) and C1/C2 (advanced) levels.
                    5. The final output MUST be a JSON array of objects, with each object representing a single question.
                    6. Each question object MUST have the following keys:
                        - `question`: The text of the question.
                        - `options`: An array of 4 string options.
                        - `correctAnswer`: The correct option string.
                    7. The response MUST be ONLY the raw, minified JSON string. Do NOT wrap it in Markdown code blocks.
                    
                    --- EXAMPLES ---
                    
                    Example 1:
                    Language: English
                    Number of questions: 2
                    JSON Output:
                    [{"question":"What is the plural of 'child'?","options":["childs","children","child's","childen"],"correctAnswer":"children"},
                    {"question":"Choose the correct sentence:","options":["I am go to the store","He is going to the store","She are going to the store","They am going to the store"],"correctAnswer":"He is going to the store"}]
                    
                    Example 2:
                    Language: German
                    Number of questions: 2
                    JSON Output:
                    [{"question":"Wie geht es dir?","options":["Mir geht es gut","Ich bin ein Haus","Ich habe ein Buch","Sie ist mein Freund"],"correctAnswer":"Mir geht es gut"},
                    {"question":"Er will ... (to read) ein Buch.","options":["lesen","lese","lest","liest"],"correctAnswer":"lesen"}]
                    
                    --- TASK TO PERFORM ---
                    
                    Language: %s
                    Number of questions: %d
                    JSON Output:
                """, 15, currentUser.currentLang(), 15
            );
            String chatCompletion = generativeAiService.chatCompletion(AiProvider.GEMINI, promptTemplate);

            try {
                languageQuizResponseDtoList = objectMapper.readValue(chatCompletion, new TypeReference<>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e.getMessage());
            }

            telegramUtil.sendDeleteMessageRequest(message.getChatId(), message.getMessageId());

            LanguageQuizResponseDto first = languageQuizResponseDtoList.getFirst();
            String firstQuestion = "%d/15.\n%s".formatted(++questionsCount, first.question());
            List<String> firstOptions = first.options();
            CallbackCommandEnum callbackCommand = LANGUAGE_PROFICIENCY_TEST_NEXT_QUESTION;
            callbackCommand.setDescription(first.correctAnswer());
            languageQuizResponseDtoList.removeFirst();

            telegramUtil.sendMessageWithInlineKeyboard(
                message.getChatId(),
                firstQuestion,
                firstOptions,
                callbackCommand
            );

        } else if (callbackQuery.getData().endsWith(LANGUAGE_PROFICIENCY_TEST_NEXT_QUESTION.getValue())) {
            if (languageQuizResponseDtoList.isEmpty()) {
                telegramUtil.sendDeleteMessageRequest(message.getChatId(), message.getMessageId());
                questionsCount = 0;

                String analyzePromptTemplate = String.format(
                    """
                        You are a language proficiency evaluator.
                        Analyze the user's quiz results for the specified language.
                
                        INPUT:
                        - Language: %s
                        - Total correct answers: %d out of %d
                        - Questions and answers: %s  // JSON array of question objects with userAnswer and correctAnswer
                
                        TASK:
                        1. Evaluate the user's approximate proficiency level (A0, A1, A2, B1, B2, C1, or C2).
                        2. Briefly list weak areas (e.g., grammar, vocabulary, sentence structure).
                        3. Give 1–2 short recommendations for improvement.
                        4. Keep the answer concise — maximum 3 short sentences.
                        5. Your final response MUST BE ONLY the raw minified JSON text. Do NOT wrap it in Markdown code blocks like ```json or ```.
                        6. Your final response in ukrainian language only.
                
                        OUTPUT FORMAT (strict):
                        {
                          "level": "B1",
                          "weakAreas": ["grammar", "sentence structure"],
                          "recommendations": ["Review irregular verbs", "Practice building longer sentences"]
                        }
                    """, currentUser.currentLang(), correctAnswersCount, 15, userAnswers
                );

                String string = generativeAiService.chatCompletion(AiProvider.GEMINI, analyzePromptTemplate);
                LanguageTestAnalysis languageTestAnalysis;
                try {
                    string = string.replace("```json", "").replace("```", "").trim();
                    languageTestAnalysis = objectMapper.readValue(string, LanguageTestAnalysis.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e.getMessage());
                }
                String s = String.format("""
                    Ваш рівень: %s
                    Вразливі місця: %s
                    Рекомендації: %s
                    """, languageTestAnalysis.level(), languageTestAnalysis.weakAreas(), languageTestAnalysis.recommendations());
                telegramUtil.sendMessage(message.getChatId(), s, false);
            } else {
                CallbackCommandEnum callbackCommand = LANGUAGE_PROFICIENCY_TEST_NEXT_QUESTION;
                String userAnswerValue = callbackQuery.getData().replace(callbackCommand.getValue(), "");
                String correctAnswerValue = callbackCommand.getCustomDescription();
                if (userAnswerValue.equals(correctAnswerValue)) {
                    correctAnswersCount++;
                }
                UserAnswer userAnswer = new UserAnswer(
                    ((Message) message).getText(),
                    userAnswerValue,
                    callbackCommand.getCustomDescription()
                );
                userAnswers.add(userAnswer);
                LanguageQuizResponseDto first = languageQuizResponseDtoList.getFirst();
                languageQuizResponseDtoList.removeFirst();

                callbackCommand.setDescription(first.correctAnswer());
                telegramUtil.sendEditMessageWithInlineKeyboard(
                    message.getChatId(),
                    message.getMessageId(),
                    "%d/15.\n%s".formatted(++questionsCount, first.question()),
                    first.options(),
                    callbackCommand
                );
            }
        }
    }

    private record LanguageQuizResponseDto(String question, List<String> options, String correctAnswer) {}
    private record UserAnswer(String question, String userAnswer, String correctAnswer) {}
    private record LanguageTestAnalysis(String level, List<String> weakAreas, List<String> recommendations) {}

    private final UserService userService;
    private final GenerativeAiService generativeAiService;
    private final ObjectMapper objectMapper;
    private final TelegramUtil telegramUtil;
}
