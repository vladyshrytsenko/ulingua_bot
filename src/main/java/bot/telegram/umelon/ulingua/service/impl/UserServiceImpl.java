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
        User user = userRepository.findById(id).orElse(null);
        return user != null ? UserMapper.MAPPER.toDto(user) : null;
    }

    @Override
    public UserDto save(UserDto requestDto) {
        User entity = UserMapper.MAPPER.toEntity(requestDto);
        User savedUser = userRepository.save(entity);

        return UserMapper.MAPPER.toDto(savedUser);
    }

    @Override
    @Transactional
    public void addUserLanguage(long userId, LanguageDto languageDto) {
        UserDto currentUserDto = this.getById(userId);
        if (currentUserDto.languages() == null) {
            currentUserDto = currentUserDto.toBuilder()
                .languages(new HashSet<>())
                .build();
        }
        currentUserDto.languages().add(languageDto);
        currentUserDto = currentUserDto.toBuilder()
            .currentLang(languageDto.countryCode())
            .build();

        this.save(currentUserDto);
    }

    @Override
    @Transactional
    public void removeUserLanguage(long userId, long languageId) {
        LanguageDto foundLanguageDto = languageService.getById(languageId);

        UserDto currentUserDto = this.getById(userId);
        currentUserDto.languages().remove(foundLanguageDto);

        if (foundLanguageDto.countryCode().equals(currentUserDto.currentLang())) {
            Set<LanguageDto> userLanguages = currentUserDto.languages();
            LanguageDto lastUserLanguage = new ArrayList<>(userLanguages).get(userLanguages.size() - 1);
            currentUserDto = currentUserDto.toBuilder()
                .currentLang(lastUserLanguage.countryCode())
                .build();
        }

        this.save(currentUserDto);
    }

    @Override
    public void setUserCurrentLanguage(long userId, String langCode) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setCurrentLang(langCode);

            userRepository.save(user);
        }
    }

    @Override
    public void setDailyLimit(long userId, byte dailyLimit) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setDailyLimit(dailyLimit);

            userRepository.save(user);
        }
    }

    public UserState getUserState(long userId) {
        return this.userStateMap.getOrDefault(userId, null);
    }

    public void setUserState(long userId, UserState state) {
        this.userStateMap.put(userId, state);
    }

    private final UserRepository userRepository;
    private final LanguageService languageService;

    private final Map<Long, UserState> userStateMap = new HashMap<>();
}
