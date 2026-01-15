package dev.benwilliams.am_i_detained;

import dev.benwilliams.am_i_detained.dto.CheckInScheduleDto;
import dev.benwilliams.am_i_detained.entity.User;
import dev.benwilliams.am_i_detained.repository.UserRepository;
import dev.benwilliams.am_i_detained.service.CheckInScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
@DisplayName("Check-in Schedule Configuration")
class CheckInScheduleAcceptanceTest {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CheckInScheduleService scheduleService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .oauthProvider("google")
            .oauthId("test789")
            .email("test@example.com")
            .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    @DisplayName("User can create check-in schedule with time, grace period, and enable monitoring")
    void userCanCreateSchedule() {
        CheckInScheduleDto schedule = scheduleService.updateSchedule(
            testUser.getId(), 
            LocalTime.of(22, 0), 
            30, 
            true
        );

        assertAll("schedule creation",
            () -> assertThat(schedule.checkInTime()).isEqualTo(LocalTime.of(22, 0)),
            () -> assertThat(schedule.gracePeriodMinutes()).isEqualTo(30),
            () -> assertThat(schedule.enabled()).isTrue()
        );
    }

    @Test
    @DisplayName("User can update existing check-in schedule")
    void userCanUpdateSchedule() {
        scheduleService.updateSchedule(testUser.getId(), LocalTime.of(22, 0), 30, true);
        
        CheckInScheduleDto updated = scheduleService.updateSchedule(testUser.getId(), LocalTime.of(23, 0), 60, false);

        assertAll("schedule update",
            () -> assertThat(updated.checkInTime()).isEqualTo(LocalTime.of(23, 0)),
            () -> assertThat(updated.gracePeriodMinutes()).isEqualTo(60),
            () -> assertThat(updated.enabled()).isFalse()
        );
    }

    @Test
    @DisplayName("Schedule is automatically created with defaults when first accessed")
    void scheduleIsCreatedWithDefaultsIfNotExists() {
        CheckInScheduleDto schedule = scheduleService.getSchedule(testUser.getId());

        assertAll("default schedule",
            () -> assertThat(schedule).isNotNull(),
            () -> assertThat(schedule.enabled()).isFalse(),
            () -> assertThat(schedule.gracePeriodMinutes()).isEqualTo(5)
        );
    }
}
