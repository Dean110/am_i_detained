package dev.benwilliams.am_i_detained.dto;

public record UserDto(
    Long id,
    String oauthProvider,
    String oauthId,
    String email,
    String phoneNumber,
    String alias,
    String manualLocation,
    Double latitude,
    Double longitude,
    String customAlertMessage
) {}
