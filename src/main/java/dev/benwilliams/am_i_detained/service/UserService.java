package dev.benwilliams.am_i_detained.service;

import dev.benwilliams.am_i_detained.dto.UserDto;
import dev.benwilliams.am_i_detained.entity.User;
import dev.benwilliams.am_i_detained.mapper.UserMapper;
import dev.benwilliams.am_i_detained.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserDto updateProfile(Long userId, String phoneNumber, String alias, String manualLocation, String customAlertMessage) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setPhoneNumber(phoneNumber);
        user.setAlias(alias);
        user.setManualLocation(manualLocation);
        user.setCustomAlertMessage(customAlertMessage);
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public UserDto updateLocation(Long userId, Double latitude, Double longitude) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setLatitude(latitude);
        user.setLongitude(longitude);
        return userMapper.toDto(userRepository.save(user));
    }

    public UserDto getUser(Long userId) {
        return userMapper.toDto(userRepository.findById(userId).orElseThrow());
    }
}
