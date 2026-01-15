package dev.benwilliams.am_i_detained.dto;

import dev.benwilliams.am_i_detained.entity.CheckInHistory;
import java.time.LocalDateTime;

public record CheckInHistoryDto(
    Long id,
    Long userId,
    LocalDateTime checkInTimestamp,
    CheckInHistory.CheckInType type
) {}
