package dev.benwilliams.am_i_detained.batch;

import dev.benwilliams.am_i_detained.entity.CheckInSchedule;
import dev.benwilliams.am_i_detained.entity.Contact;
import dev.benwilliams.am_i_detained.entity.User;
import dev.benwilliams.am_i_detained.repository.CheckInScheduleRepository;
import dev.benwilliams.am_i_detained.repository.ContactRepository;
import dev.benwilliams.am_i_detained.repository.UserRepository;
import dev.benwilliams.am_i_detained.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class CheckInMonitorJob {
    private static final Logger log = LoggerFactory.getLogger(CheckInMonitorJob.class);
    
    private final CheckInScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final NotificationService notificationService;

    public CheckInMonitorJob(CheckInScheduleRepository scheduleRepository,
                             UserRepository userRepository,
                             ContactRepository contactRepository,
                             NotificationService notificationService) {
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
        this.notificationService = notificationService;
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    public void monitorCheckIns() {
        List<CheckInSchedule> schedules = scheduleRepository.findByEnabledTrue();
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        LocalDate today = now.toLocalDate();

        for (CheckInSchedule schedule : schedules) {
            if (schedule.getCheckInTime() == null) continue;

            boolean isOverdue = currentTime.isAfter(schedule.getCheckInTime());
            boolean checkedInToday = schedule.getLastCheckIn() != null && 
                                     schedule.getLastCheckIn().toLocalDate().equals(today);

            if (isOverdue && !checkedInToday) {
                User user = userRepository.findById(schedule.getUserId()).orElseThrow();
                
                if (schedule.getReminderSentAt() == null) {
                    log.info("Sending reminder to user: {}", user.getEmail());
                    notificationService.sendReminder(user);
                    schedule.setReminderSentAt(now);
                    scheduleRepository.save(schedule);
                } else {
                    LocalDateTime gracePeriodEnd = schedule.getReminderSentAt()
                        .plusMinutes(schedule.getGracePeriodMinutes());
                    
                    if (now.isAfter(gracePeriodEnd)) {
                        log.info("Grace period expired, sending emergency alerts for user: {}", user.getEmail());
                        List<Contact> contacts = contactRepository.findByUserId(user.getId());
                        notificationService.sendEmergencyAlert(user, contacts);
                        
                        schedule.setReminderSentAt(null);
                        schedule.setLastCheckIn(now);
                        scheduleRepository.save(schedule);
                    }
                }
            }
        }
    }
}
