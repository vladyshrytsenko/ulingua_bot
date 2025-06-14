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
        Optional<User> existingUserOpt = this.userRepository.findById(user.getId());
        User savedUser;

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            existingUser.setFirstname(user.getFirstname());
            existingUser.setLastname(user.getLastname());
            existingUser.setUsername(user.getUsername());

            if (user.getNativeLang() != null) {
                existingUser.setNativeLang(user.getNativeLang());
            }
            if (user.getCurrentLang() != null) {
                existingUser.setCurrentLang(user.getCurrentLang());
            }
            if (user.getLanguages() != null) {
                existingUser.setLanguages(user.getLanguages());
            }

            savedUser = this.userRepository.save(existingUser);
        } else {
            savedUser = this.userRepository.save(user);
        }

        return UserMapper.MAPPER.toDto(savedUser);
    }

    @Override
    @Transactional
    public void addUserLanguage(long userId, long languageId) {
        LanguageDto foundLanguageDto = this.languageService.getById(languageId);

        UserDto currentUser = getById(userId);
        if (currentUser.languages() == null) {
            currentUser = currentUser.toBuilder()
                .languages(new HashSet<>())
                .build();
        }
        currentUser.languages().add(foundLanguageDto);
        currentUser = currentUser.toBuilder()
            .currentLang(foundLanguageDto.countryCode())
            .build();

        User currentUserEntity = UserMapper.MAPPER.toEntity(currentUser);
        save(currentUserEntity);
    }

    @Override
    @Transactional
    public void removeUserLanguage(long userId, long languageId) {
        LanguageDto foundLanguageDto = this.languageService.getById(languageId);

        UserDto currentUser = getById(userId);
        currentUser.languages().remove(foundLanguageDto);

        if (foundLanguageDto.countryCode().equals(currentUser.currentLang())) {
            Set<LanguageDto> userLanguages = currentUser.languages();
            LanguageDto lastUserLanguage = new ArrayList<>(userLanguages).get(userLanguages.size() - 1);
            currentUser = currentUser.toBuilder()
                .currentLang(lastUserLanguage.countryCode())
                .build();
        }

        User currentUserEntity = UserMapper.MAPPER.toEntity(currentUser);
        save(currentUserEntity);
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
