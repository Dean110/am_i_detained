package dev.benwilliams.am_i_detained.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_oauth", columnList = "oauthProvider,oauthId", unique = true)
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String oauthProvider;
    private String oauthId;
    private String email;
    private String phoneNumber;
    private String alias;
    private String manualLocation;
    private Double latitude;
    private Double longitude;
    
    @Column(length = 1000)
    private String customAlertMessage;
    
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
