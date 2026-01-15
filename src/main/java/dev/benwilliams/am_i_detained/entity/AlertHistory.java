package dev.benwilliams.am_i_detained.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alert_history", indexes = {
    @Index(name = "idx_user_alert_timestamp", columnList = "userId,alertTimestamp")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class AlertHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    private LocalDateTime alertTimestamp;
    
    @Enumerated(EnumType.STRING)
    private AlertType type;
    
    @Column(length = 2000)
    private String message;
    
    private Boolean successful;
    
    @PrePersist
    protected void onCreate() {
        alertTimestamp = LocalDateTime.now();
    }
    
    public enum AlertType {
        REMINDER, EMERGENCY
    }
}
