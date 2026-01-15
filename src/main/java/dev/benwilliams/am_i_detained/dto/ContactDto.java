package dev.benwilliams.am_i_detained.dto;

public record ContactDto(
    Long id,
    Long userId,
    String alias,
    String phoneNumber,
    String email
) {}
