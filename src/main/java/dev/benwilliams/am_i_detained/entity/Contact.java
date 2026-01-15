package dev.benwilliams.am_i_detained.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contacts", indexes = {
    @Index(name = "idx_user_id", columnList = "userId")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    private String alias;
    private String phoneNumber;
    private String email;
}
