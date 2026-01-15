package dev.benwilliams.am_i_detained.exception;

import java.time.LocalDateTime;

public record ErrorDetails(
    LocalDateTime timestamp,
    int status,
    String error,
    String message
) {}
