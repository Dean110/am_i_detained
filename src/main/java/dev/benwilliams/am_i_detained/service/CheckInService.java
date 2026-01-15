package dev.benwilliams.am_i_detained.service;

import dev.benwilliams.am_i_detained.dto.CheckInHistoryDto;
import dev.benwilliams.am_i_detained.entity.CheckInHistory;
import dev.benwilliams.am_i_detained.entity.CheckInSchedule;
import dev.benwilliams.am_i_detained.entity.Contact;
import dev.benwilliams.am_i_detained.entity.User;
import dev.benwilliams.am_i_detained.mapper.CheckInHistoryMapper;
import dev.benwilliams.am_i_detained.repository.CheckInHistoryRepository;
import dev.benwilliams.am_i_detained.repository.CheckInScheduleRepository;
import dev.benwilliams.am_i_detained.repository.ContactRepository;
import dev.benwilliams.am_i_detained.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckInService {
    private final CheckInScheduleRepository scheduleRepository;
    private final CheckInHistoryRepository historyRepository;
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final CheckInHistoryMapper historyMapper;

    public CheckInService(CheckInScheduleRepository scheduleRepository,
                          CheckInHistoryRepository historyRepository,
                          ContactRepository contactRepository,
                          UserRepository userRepository,
                          NotificationService notificationService,
                          CheckInHistoryMapper historyMapper) {
        this.scheduleRepository = scheduleRepository;
        this.historyRepository = historyRepository;
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.historyMapper = historyMapper;
    }

    @Transactional
    public void manualCheckIn(Long userId) {
        CheckInSchedule schedule = scheduleRepository.findByUserId(userId).orElseThrow();
        schedule.setLastCheckIn(LocalDateTime.now());
        schedule.setReminderSentAt(null);
        scheduleRepository.save(schedule);

        CheckInHistory history = CheckInHistory.builder()
            .userId(userId)
            .type(CheckInHistory.CheckInType.MANUAL)
            .build();
        historyRepository.save(history);
    }

    @Transactional
    public void triggerEmergencyAlert(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<Contact> contacts = contactRepository.findByUserId(userId);
        notificationService.sendEmergencyAlert(user, contacts);
    }

    public List<CheckInHistoryDto> getHistory(Long userId, int days) {
        return historyMapper.toDtoList(
            historyRepository.findByUserIdAndCheckInTimestampAfterOrderByCheckInTimestampDesc(
                userId,
                LocalDateTime.now().minusDays(days)
            )
        );
    }
}
