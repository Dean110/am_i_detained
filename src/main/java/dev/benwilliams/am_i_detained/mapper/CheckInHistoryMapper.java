package dev.benwilliams.am_i_detained.mapper;

import dev.benwilliams.am_i_detained.dto.CheckInHistoryDto;
import dev.benwilliams.am_i_detained.entity.CheckInHistory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CheckInHistoryMapper {

    public CheckInHistoryDto toDto(CheckInHistory entity) {
        if (entity == null) return null;
        return new CheckInHistoryDto(
            entity.getId(),
            entity.getUserId(),
            entity.getCheckInTimestamp(),
            entity.getType()
        );
    }

    public List<CheckInHistoryDto> toDtoList(List<CheckInHistory> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    public CheckInHistory toEntity(CheckInHistoryDto dto) {
        if (dto == null) return null;
        return CheckInHistory.builder()
            .id(dto.id())
            .userId(dto.userId())
            .checkInTimestamp(dto.checkInTimestamp())
            .type(dto.type())
            .build();
    }
}
