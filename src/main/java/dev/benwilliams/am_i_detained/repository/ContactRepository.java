package dev.benwilliams.am_i_detained.repository;

import dev.benwilliams.am_i_detained.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByUserId(Long userId);
}
