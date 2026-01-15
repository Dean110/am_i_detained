package dev.benwilliams.am_i_detained.dto;

import java.time.LocalTime;
import java.time.LocalDateTime;

public record CheckInScheduleDto(
    Long id,
    Long userId,
    LocalTime checkInTime,
    Boolean enabled,
    LocalDateTime lastCheckIn,
    LocalDateTime reminderSentAt,
    Integer gracePeriodMinutes
) {}
