package bot.telegram.umelon.ulingua.service.impl;

import bot.telegram.umelon.ulingua.model.dto.LanguageDto;
import bot.telegram.umelon.ulingua.model.dto.UserDto;
import bot.telegram.umelon.ulingua.model.entity.User;
import bot.telegram.umelon.ulingua.model.enums.UserState;
import bot.telegram.umelon.ulingua.model.mapper.UserMapper;
import bot.telegram.umelon.ulingua.service.LanguageService;
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

    @Override
    public UserDto getById(long id) {
        User user = this.userRepository.findById(id).orElse(null);
        return user != null ? UserMapper.MAPPER.toDto(user) : null;
    }

    @Override
    public UserDto save(User user) {
        User savedUser = this.userRepository.save(user);
        return UserMapper.MAPPER.toDto(savedUser);
    }

    @Override
    @Transactional
    public void addUserLanguage(long userId, LanguageDto languageDto) {
        UserDto currentUser = this.getById(userId);
        if (currentUser.languages() == null) {
            currentUser = currentUser.toBuilder()
                .languages(new HashSet<>())
                .build();
        }
        currentUser.languages().add(languageDto);
        currentUser = currentUser.toBuilder()
            .currentLang(languageDto.countryCode())
            .build();

        User currentUserEntity = UserMapper.MAPPER.toEntity(currentUser);
        this.save(currentUserEntity);
    }

    @Override
    @Transactional
    public void removeUserLanguage(long userId, long languageId) {
        LanguageDto foundLanguageDto = this.languageService.getById(languageId);

        UserDto currentUser = this.getById(userId);
        currentUser.languages().remove(foundLanguageDto);

        if (foundLanguageDto.countryCode().equals(currentUser.currentLang())) {
            Set<LanguageDto> userLanguages = currentUser.languages();
            LanguageDto lastUserLanguage = new ArrayList<>(userLanguages).get(userLanguages.size() - 1);
            currentUser = currentUser.toBuilder()
                .currentLang(lastUserLanguage.countryCode())
                .build();
        }

        User currentUserEntity = UserMapper.MAPPER.toEntity(currentUser);
        this.save(currentUserEntity);
    }

    @Override
    public void setUserCurrentLanguage(long userId, String langCode) {
        Optional<User> userOptional = this.userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setCurrentLang(langCode);

            this.userRepository.save(user);
        }
    }

    @Override
    public void setDailyLimit(long userId, byte dailyLimit) {
        Optional<User> userOptional = this.userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setDailyLimit(dailyLimit);

            this.userRepository.save(user);
        }
    }

    public UserState getUserState(long userId) {
        return userStateMap.getOrDefault(userId, null);
    }

    public void setUserState(long userId, UserState state) {
        userStateMap.put(userId, state);
    }

    private final UserRepository userRepository;
    private final LanguageService languageService;

    private final Map<Long, UserState> userStateMap = new HashMap<>();
}
