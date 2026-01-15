package dev.benwilliams.am_i_detained;

import dev.benwilliams.am_i_detained.dto.UserDto;
import dev.benwilliams.am_i_detained.entity.User;
import dev.benwilliams.am_i_detained.repository.UserRepository;
import dev.benwilliams.am_i_detained.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
@DisplayName("User Profile Management")
class UserProfileAcceptanceTest {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .oauthProvider("google")
            .oauthId("test123")
            .email("test@example.com")
            .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    @DisplayName("User can update profile with phone, alias, location, and custom message")
    void userCanUpdateProfile() {
        userService.updateProfile(testUser.getId(), "+1234567890", "John Doe", "123 Main St", "Emergency");

        UserDto updated = userService.getUser(testUser.getId());
        assertAll("profile updates",
            () -> assertThat(updated.phoneNumber()).isEqualTo("+1234567890"),
            () -> assertThat(updated.alias()).isEqualTo("John Doe"),
            () -> assertThat(updated.manualLocation()).isEqualTo("123 Main St"),
            () -> assertThat(updated.customAlertMessage()).isEqualTo("Emergency")
        );
    }

    @Test
    @DisplayName("User can update GPS location with latitude and longitude")
    void userCanUpdateGPSLocation() {
        userService.updateLocation(testUser.getId(), 40.7128, -74.0060);

        UserDto updated = userService.getUser(testUser.getId());
        assertAll("GPS location",
            () -> assertThat(updated.latitude()).isEqualTo(40.7128),
            () -> assertThat(updated.longitude()).isEqualTo(-74.0060)
        );
    }
}
