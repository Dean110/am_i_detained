package dev.benwilliams.am_i_detained;

import dev.benwilliams.am_i_detained.dto.CheckInHistoryDto;
import dev.benwilliams.am_i_detained.entity.*;
import dev.benwilliams.am_i_detained.repository.*;
import dev.benwilliams.am_i_detained.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
@DisplayName("Check-in Flow")
class CheckInFlowAcceptanceTest {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CheckInService checkInService;
    
    @Autowired
    private CheckInScheduleRepository scheduleRepository;
    
    @Autowired
    private CheckInScheduleService scheduleService;
    
    @Autowired
    private ContactService contactService;
    
    @Autowired
    private AlertHistoryRepository alertHistoryRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .oauthProvider("google")
            .oauthId("test999")
            .email("test@example.com")
            .alias("Test User")
            .build();
        testUser = userRepository.save(testUser);
        
        scheduleService.getSchedule(testUser.getId());
        contactService.createContact(testUser.getId(), "Emergency Contact", "+1234567890", "emergency@example.com");
    }

    @Test
    @DisplayName("User can manually check in and timestamp is recorded")
    void userCanManuallyCheckIn() {
        checkInService.manualCheckIn(testUser.getId());

        CheckInSchedule schedule = scheduleRepository.findByUserId(testUser.getId()).orElseThrow();
        assertAll("manual check-in",
            () -> assertThat(schedule.getLastCheckIn()).isNotNull(),
            () -> assertThat(schedule.getLastCheckIn()).isAfter(LocalDateTime.now().minusMinutes(1))
        );
    }

    @Test
    @DisplayName("Manual check-in clears reminder flag")
    void manualCheckInClearsReminderFlag() {
        CheckInSchedule schedule = scheduleRepository.findByUserId(testUser.getId()).orElseThrow();
        schedule.setReminderSentAt(LocalDateTime.now().minusMinutes(10));
        scheduleRepository.save(schedule);

        checkInService.manualCheckIn(testUser.getId());

        schedule = scheduleRepository.findByUserId(testUser.getId()).orElseThrow();
        assertThat(schedule.getReminderSentAt()).isNull();
    }

    @Test
    @DisplayName("User can trigger emergency alert and contacts are notified")
    void userCanTriggerEmergencyAlert() {
        checkInService.triggerEmergencyAlert(testUser.getId());

        List<AlertHistory> alerts = alertHistoryRepository.findByUserIdAndAlertTimestampAfterOrderByAlertTimestampDesc(
            testUser.getId(),
            LocalDateTime.now().minusMinutes(1)
        );

        assertAll("emergency alert",
            () -> assertThat(alerts).hasSize(1),
            () -> assertThat(alerts.get(0).getType()).isEqualTo(AlertHistory.AlertType.EMERGENCY),
            () -> assertThat(alerts.get(0).getSuccessful()).isTrue()
        );
    }

    @Test
    @DisplayName("Check-in history is recorded with correct type")
    void checkInHistoryIsRecorded() {
        checkInService.manualCheckIn(testUser.getId());

        List<CheckInHistoryDto> history = checkInService.getHistory(testUser.getId(), 1);

        assertAll("check-in history",
            () -> assertThat(history).hasSize(1),
            () -> assertThat(history.get(0).type()).isEqualTo(CheckInHistory.CheckInType.MANUAL)
        );
    }
}
