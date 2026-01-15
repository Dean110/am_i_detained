package dev.benwilliams.am_i_detained.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "check_in_schedules", indexes = {
    @Index(name = "idx_user_schedule", columnList = "userId", unique = true)
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CheckInSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    private LocalTime checkInTime;
    
    @Builder.Default
    private Boolean enabled = false;
    
    private LocalDateTime lastCheckIn;
    private LocalDateTime reminderSentAt;
    
    @Builder.Default
    private Integer gracePeriodMinutes = 5;
}
