package dev.benwilliams.am_i_detained.mapper;

import dev.benwilliams.am_i_detained.dto.ContactDto;
import dev.benwilliams.am_i_detained.entity.Contact;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContactMapper {

    public ContactDto toDto(Contact entity) {
        if (entity == null) return null;
        return new ContactDto(
            entity.getId(),
            entity.getUserId(),
            entity.getAlias(),
            entity.getPhoneNumber(),
            entity.getEmail()
        );
    }

    public List<ContactDto> toDtoList(List<Contact> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    public Contact toEntity(ContactDto dto) {
        if (dto == null) return null;
        return Contact.builder()
            .id(dto.id())
            .userId(dto.userId())
            .alias(dto.alias())
            .phoneNumber(dto.phoneNumber())
            .email(dto.email())
            .build();
    }
}
