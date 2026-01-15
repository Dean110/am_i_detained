package dev.benwilliams.am_i_detained.mapper;

import dev.benwilliams.am_i_detained.dto.UserDto;
import dev.benwilliams.am_i_detained.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User entity) {
        if (entity == null) return null;
        return new UserDto(
            entity.getId(),
            entity.getOauthProvider(),
            entity.getOauthId(),
            entity.getEmail(),
            entity.getPhoneNumber(),
            entity.getAlias(),
            entity.getManualLocation(),
            entity.getLatitude(),
            entity.getLongitude(),
            entity.getCustomAlertMessage()
        );
    }

    public User toEntity(UserDto dto) {
        if (dto == null) return null;
        return User.builder()
            .id(dto.id())
            .oauthProvider(dto.oauthProvider())
            .oauthId(dto.oauthId())
            .email(dto.email())
            .phoneNumber(dto.phoneNumber())
            .alias(dto.alias())
            .manualLocation(dto.manualLocation())
            .latitude(dto.latitude())
            .longitude(dto.longitude())
            .customAlertMessage(dto.customAlertMessage())
            .build();
    }
}
