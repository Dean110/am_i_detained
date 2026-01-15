package dev.benwilliams.am_i_detained.mapper;

import dev.benwilliams.am_i_detained.dto.AlertHistoryDto;
import dev.benwilliams.am_i_detained.entity.AlertHistory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AlertHistoryMapper {

    public AlertHistoryDto toDto(AlertHistory entity) {
        if (entity == null) return null;
        return new AlertHistoryDto(
            entity.getId(),
            entity.getUserId(),
            entity.getAlertTimestamp(),
            entity.getType(),
            entity.getMessage(),
            entity.getSuccessful()
        );
    }

    public List<AlertHistoryDto> toDtoList(List<AlertHistory> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    public AlertHistory toEntity(AlertHistoryDto dto) {
        if (dto == null) return null;
        return AlertHistory.builder()
            .id(dto.id())
            .userId(dto.userId())
            .alertTimestamp(dto.alertTimestamp())
            .type(dto.type())
            .message(dto.message())
            .successful(dto.successful())
            .build();
    }
}
