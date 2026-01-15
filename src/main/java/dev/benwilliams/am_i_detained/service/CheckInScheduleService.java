package dev.benwilliams.am_i_detained.service;

import dev.benwilliams.am_i_detained.dto.CheckInScheduleDto;
import dev.benwilliams.am_i_detained.entity.CheckInSchedule;
import dev.benwilliams.am_i_detained.mapper.CheckInScheduleMapper;
import dev.benwilliams.am_i_detained.repository.CheckInScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
public class CheckInScheduleService {
    private final CheckInScheduleRepository scheduleRepository;
    private final CheckInScheduleMapper scheduleMapper;

    public CheckInScheduleService(CheckInScheduleRepository scheduleRepository, CheckInScheduleMapper scheduleMapper) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
    }

    public CheckInScheduleDto getSchedule(Long userId) {
        return scheduleRepository.findByUserId(userId)
            .map(scheduleMapper::toDto)
            .orElseGet(() -> {
                CheckInSchedule schedule = CheckInSchedule.builder()
                    .userId(userId)
                    .build();
                return scheduleMapper.toDto(scheduleRepository.save(schedule));
            });
    }

    @Transactional
    public CheckInScheduleDto updateSchedule(Long userId, LocalTime checkInTime, Integer gracePeriodMinutes, Boolean enabled) {
        CheckInSchedule schedule = scheduleRepository.findByUserId(userId)
            .orElseGet(() -> CheckInSchedule.builder()
                .userId(userId)
                .build());
        schedule.setCheckInTime(checkInTime);
        schedule.setGracePeriodMinutes(gracePeriodMinutes);
        schedule.setEnabled(enabled);
        return scheduleMapper.toDto(scheduleRepository.save(schedule));
    }
}
