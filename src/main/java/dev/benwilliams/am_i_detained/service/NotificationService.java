package dev.benwilliams.am_i_detained.service;

import dev.benwilliams.am_i_detained.entity.Contact;
import dev.benwilliams.am_i_detained.entity.User;

import java.util.List;

public interface NotificationService {
    void sendReminder(User user);
    void sendEmergencyAlert(User user, List<Contact> contacts);
}
