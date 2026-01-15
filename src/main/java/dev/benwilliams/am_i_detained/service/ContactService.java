package dev.benwilliams.am_i_detained.service;

import dev.benwilliams.am_i_detained.dto.ContactDto;
import dev.benwilliams.am_i_detained.entity.Contact;
import dev.benwilliams.am_i_detained.mapper.ContactMapper;
import dev.benwilliams.am_i_detained.repository.ContactRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContactService {
    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;

    public ContactService(ContactRepository contactRepository, ContactMapper contactMapper) {
        this.contactRepository = contactRepository;
        this.contactMapper = contactMapper;
    }

    public List<ContactDto> getContacts(Long userId) {
        return contactMapper.toDtoList(contactRepository.findByUserId(userId));
    }

    @Transactional
    public ContactDto createContact(Long userId, String alias, String phoneNumber, String email) {
        Contact contact = Contact.builder()
            .userId(userId)
            .alias(alias)
            .phoneNumber(phoneNumber)
            .email(email)
            .build();
        return contactMapper.toDto(contactRepository.save(contact));
    }

    @Transactional
    public ContactDto updateContact(Long contactId, Long userId, String alias, String phoneNumber, String email) {
        Contact contact = contactRepository.findById(contactId).orElseThrow();
        if (!contact.getUserId().equals(userId)) {
            throw new SecurityException("Unauthorized");
        }
        contact.setAlias(alias);
        contact.setPhoneNumber(phoneNumber);
        contact.setEmail(email);
        return contactMapper.toDto(contactRepository.save(contact));
    }

    @Transactional
    public void deleteContact(Long contactId, Long userId) {
        Contact contact = contactRepository.findById(contactId).orElseThrow();
        if (!contact.getUserId().equals(userId)) {
            throw new SecurityException("Unauthorized");
        }
        contactRepository.delete(contact);
    }
}
