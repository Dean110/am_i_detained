package dev.benwilliams.am_i_detained.service;

import dev.benwilliams.am_i_detained.entity.AlertHistory;
import dev.benwilliams.am_i_detained.entity.Contact;
import dev.benwilliams.am_i_detained.entity.User;
import dev.benwilliams.am_i_detained.repository.AlertHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Primary
public class MockNotificationService implements NotificationService {
    private static final Logger log = LoggerFactory.getLogger(MockNotificationService.class);
    private final AlertHistoryRepository alertHistoryRepository;

    public MockNotificationService(AlertHistoryRepository alertHistoryRepository) {
        this.alertHistoryRepository = alertHistoryRepository;
    }

    @Override
    public void sendReminder(User user) {
        String message = String.format("REMINDER: %s, please check in! Time: %s",
            user.getAlias() != null ? user.getAlias() : user.getEmail(),
            LocalDateTime.now());
        
        log.info("=== MOCK REMINDER ===");
        log.info("To: {} ({})", user.getEmail(), user.getPhoneNumber());
        log.info("Message: {}", message);
        log.info("====================");

        AlertHistory alert = AlertHistory.builder()
            .userId(user.getId())
            .type(AlertHistory.AlertType.REMINDER)
            .message(message)
            .successful(true)
            .build();
        alertHistoryRepository.save(alert);
    }

    @Override
    public void sendEmergencyAlert(User user, List<Contact> contacts) {
        String location = user.getLatitude() != null && user.getLongitude() != null
            ? String.format("GPS: %.6f, %.6f", user.getLatitude(), user.getLongitude())
            : user.getManualLocation() != null ? user.getManualLocation() : "Unknown";

        String message = String.format("EMERGENCY ALERT: %s needs help! Location: %s. Message: %s",
            user.getAlias() != null ? user.getAlias() : user.getEmail(),
            location,
            user.getCustomAlertMessage() != null ? user.getCustomAlertMessage() : "No additional message");

        log.info("=== MOCK EMERGENCY ALERT ===");
        log.info("From: {} ({})", user.getAlias(), user.getEmail());
        log.info("Message: {}", message);
        
        for (Contact contact : contacts) {
            log.info("Sending to: {} - Phone: {} - Email: {}", contact.getAlias(), contact.getPhoneNumber(), contact.getEmail());
        }
        log.info("===========================");

        AlertHistory alert = AlertHistory.builder()
            .userId(user.getId())
            .type(AlertHistory.AlertType.EMERGENCY)
            .message(message)
            .successful(true)
            .build();
        alertHistoryRepository.save(alert);
    }
}
