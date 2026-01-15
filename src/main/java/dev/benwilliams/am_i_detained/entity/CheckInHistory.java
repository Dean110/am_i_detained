package dev.benwilliams.am_i_detained.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "check_in_history", indexes = {
    @Index(name = "idx_user_timestamp", columnList = "userId,checkInTimestamp")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CheckInHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    private LocalDateTime checkInTimestamp;
    
    @Enumerated(EnumType.STRING)
    private CheckInType type;
    
    @PrePersist
    protected void onCreate() {
        checkInTimestamp = LocalDateTime.now();
    }
    
    public enum CheckInType {
        MANUAL, AUTO, MISSED
    }
}
