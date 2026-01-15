package dev.benwilliams.am_i_detained.mapper;

import dev.benwilliams.am_i_detained.dto.CheckInScheduleDto;
import dev.benwilliams.am_i_detained.entity.CheckInSchedule;
import org.springframework.stereotype.Component;

@Component
public class CheckInScheduleMapper {

    public CheckInScheduleDto toDto(CheckInSchedule entity) {
        if (entity == null) return null;
        return new CheckInScheduleDto(
            entity.getId(),
            entity.getUserId(),
            entity.getCheckInTime(),
            entity.getEnabled(),
            entity.getLastCheckIn(),
            entity.getReminderSentAt(),
            entity.getGracePeriodMinutes()
        );
    }

    public CheckInSchedule toEntity(CheckInScheduleDto dto) {
        if (dto == null) return null;
        return CheckInSchedule.builder()
            .id(dto.id())
            .userId(dto.userId())
            .checkInTime(dto.checkInTime())
            .enabled(dto.enabled())
            .lastCheckIn(dto.lastCheckIn())
            .reminderSentAt(dto.reminderSentAt())
            .gracePeriodMinutes(dto.gracePeriodMinutes())
            .build();
    }
}
