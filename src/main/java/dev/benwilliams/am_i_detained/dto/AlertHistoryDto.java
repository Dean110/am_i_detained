package dev.benwilliams.am_i_detained.dto;

import dev.benwilliams.am_i_detained.entity.AlertHistory;
import java.time.LocalDateTime;

public record AlertHistoryDto(
    Long id,
    Long userId,
    LocalDateTime alertTimestamp,
    AlertHistory.AlertType type,
    String message,
    Boolean successful
) {}
