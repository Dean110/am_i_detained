package dev.benwilliams.am_i_detained;

import dev.benwilliams.am_i_detained.dto.ContactDto;
import dev.benwilliams.am_i_detained.entity.User;
import dev.benwilliams.am_i_detained.repository.UserRepository;
import dev.benwilliams.am_i_detained.service.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
@DisplayName("Contact Management")
class ContactManagementAcceptanceTest {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ContactService contactService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .oauthProvider("google")
            .oauthId("test456")
            .email("test@example.com")
            .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    @DisplayName("User can create emergency contact with alias, phone, and email")
    void userCanCreateContact() {
        ContactDto contact = contactService.createContact(testUser.getId(), "Jane Doe", "+9876543210", "jane@example.com");

        assertAll("contact creation",
            () -> assertThat(contact.id()).isNotNull(),
            () -> assertThat(contact.alias()).isEqualTo("Jane Doe"),
            () -> assertThat(contact.phoneNumber()).isEqualTo("+9876543210"),
            () -> assertThat(contact.email()).isEqualTo("jane@example.com")
        );
    }

    @Test
    @DisplayName("User can list all their emergency contacts")
    void userCanListContacts() {
        contactService.createContact(testUser.getId(), "Contact 1", "+1111111111", "c1@example.com");
        contactService.createContact(testUser.getId(), "Contact 2", "+2222222222", "c2@example.com");

        List<ContactDto> contacts = contactService.getContacts(testUser.getId());
        assertThat(contacts).hasSize(2);
    }

    @Test
    @DisplayName("User can update existing contact information")
    void userCanUpdateContact() {
        ContactDto contact = contactService.createContact(testUser.getId(), "Old Name", "+1111111111", "old@example.com");

        ContactDto updated = contactService.updateContact(contact.id(), testUser.getId(), "New Name", "+9999999999", "new@example.com");

        assertAll("contact update",
            () -> assertThat(updated.alias()).isEqualTo("New Name"),
            () -> assertThat(updated.phoneNumber()).isEqualTo("+9999999999"),
            () -> assertThat(updated.email()).isEqualTo("new@example.com")
        );
    }

    @Test
    @DisplayName("User can delete emergency contact")
    void userCanDeleteContact() {
        ContactDto contact = contactService.createContact(testUser.getId(), "To Delete", "+1111111111", "delete@example.com");

        contactService.deleteContact(contact.id(), testUser.getId());

        List<ContactDto> contacts = contactService.getContacts(testUser.getId());
        assertThat(contacts).isEmpty();
    }

    @Test
    @DisplayName("User cannot access or modify other users' contacts")
    void userCannotAccessOtherUsersContacts() {
        User otherUser = User.builder()
            .oauthProvider("google")
            .oauthId("other789")
            .email("other@example.com")
            .build();
        User savedOtherUser = userRepository.save(otherUser);

        ContactDto contact = contactService.createContact(testUser.getId(), "My Contact", "+1111111111", "my@example.com");

        assertThatThrownBy(() -> contactService.deleteContact(contact.id(), savedOtherUser.getId()))
            .isInstanceOf(SecurityException.class);
    }
}
