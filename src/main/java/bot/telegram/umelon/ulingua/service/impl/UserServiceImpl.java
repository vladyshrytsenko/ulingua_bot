package bot.telegram.umelon.ulingua.service.impl;

import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.entity.User;
import bot.telegram.umelon.ulingua.model.entity.UserWord;
import bot.telegram.umelon.ulingua.model.enums.UserState;
import bot.telegram.umelon.ulingua.repository.UserWordRepository;
import bot.telegram.umelon.ulingua.service.LanguageService;
import bot.telegram.umelon.ulingua.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import bot.telegram.umelon.ulingua.repository.UserRepository;
import bot.telegram.umelon.ulingua.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final LanguageService languageService;
    private final UserWordRepository userWordRepository;
    private final WordService wordService;

    private Map<Long, UserState> userStateMap = new HashMap<>();

    public void setUserState(long chatId, UserState state) {
        userStateMap.put(chatId, state);
    }

    public UserState getUserState(long chatId) {
        return userStateMap.getOrDefault(chatId, null);
    }

    @Override
    public UserDto getById(long id) {
        User user = userRepository.findById(id).orElse(null);
        return user != null ? UserDto.toDto(user) : null;
    }

    @Override
    public UserDto getByChatId(long id) {
        User user = userRepository.findByChatId(id).orElse(null);
        return user != null ? UserDto.toDto(user) : null;
    }

    @Override
    public UserDto save(User user) {
        Optional<User> existingUserOpt = userRepository.findByChatId(user.getChatId());
        User savedUser;

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setUsername(user.getUsername());

            if (user.getNativeLang() != null) {
                existingUser.setNativeLang(user.getNativeLang());
            }
            if (user.getCurrentLang() != null) {
                existingUser.setCurrentLang(user.getCurrentLang());
            }
            if (user.getWords() != null) {
                existingUser.setWords(user.getWords());
            }
            if (user.getLanguages() != null) {
                existingUser.setLanguages(user.getLanguages());
            }

            savedUser = userRepository.save(existingUser);
        } else {
            savedUser = userRepository.save(user);
        }

        return UserDto.toDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto addUserLanguage(long chatId, long languageId) {
        LanguageDto foundLanguageDto = languageService.getById(languageId);

        UserDto currentUser = getByChatId(chatId);
        if (currentUser.getLanguages() == null) {
            currentUser.setLanguages(new HashSet<>());
        }
        currentUser.getLanguages().add(foundLanguageDto);
        currentUser.setCurrentLang(foundLanguageDto.getCountryCode());

        User currentUserEntity = UserDto.toEntity(currentUser);
        return save(currentUserEntity);
    }

    @Override
    @Transactional
    public UserDto removeUserLanguage(long chatId, long languageId) {
        LanguageDto foundLanguageDto = languageService.getById(languageId);

        UserDto currentUser = getByChatId(chatId);
        currentUser.getLanguages().remove(foundLanguageDto);

        if (foundLanguageDto.getCountryCode().equals(currentUser.getCurrentLang())) {
            Set<LanguageDto> userLanguages = currentUser.getLanguages();
            LanguageDto lastUserLanguage = new ArrayList<>(userLanguages).get(userLanguages.size() - 1);
            currentUser.setCurrentLang(lastUserLanguage.getCountryCode());
        }

        User currentUserEntity = UserDto.toEntity(currentUser);
        return save(currentUserEntity);
    }

    @Override
    public UserDto setCurrentLanguageForUser(long chatId, String langCode) {
        Optional<User> userOptional = userRepository.findByChatId(chatId);
        if (userOptional.isPresent()) {
            bot.telegram.umelon.ulingua.model.entity.User user = userOptional.get();

            user.setCurrentLang(langCode);
            User updated = userRepository.save(user);
            return UserDto.toDto(updated);
        }
        return null;
    }

    @Override
    @Transactional
    public void addWordForUser(long userId, long wordId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        UserWord userWord = UserWord.builder()
            .userId(userId)
            .wordId(wordId)
            .build();

        userWordRepository.save(userWord);
    }
}
