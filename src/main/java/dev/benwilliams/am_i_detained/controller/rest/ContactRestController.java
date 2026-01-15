package dev.benwilliams.am_i_detained.controller.rest;

import dev.benwilliams.am_i_detained.dto.ContactDto;
import dev.benwilliams.am_i_detained.security.CustomOAuth2User;
import dev.benwilliams.am_i_detained.service.ContactService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contacts")
public class ContactRestController {
    private final ContactService contactService;

    public ContactRestController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public List<ContactDto> getContacts(@AuthenticationPrincipal CustomOAuth2User principal) {
        return contactService.getContacts(principal.getUser().getId());
    }

    @PostMapping
    public ContactDto createContact(@AuthenticationPrincipal CustomOAuth2User principal,
                                 @RequestBody Map<String, String> data) {
        return contactService.createContact(
            principal.getUser().getId(),
            data.get("alias"),
            data.get("phoneNumber"),
            data.get("email")
        );
    }

    @PutMapping("/{id}")
    public ContactDto updateContact(@AuthenticationPrincipal CustomOAuth2User principal,
                                 @PathVariable Long id,
                                 @RequestBody Map<String, String> data) {
        return contactService.updateContact(
            id,
            principal.getUser().getId(),
            data.get("alias"),
            data.get("phoneNumber"),
            data.get("email")
        );
    }

    @DeleteMapping("/{id}")
    public void deleteContact(@AuthenticationPrincipal CustomOAuth2User principal,
                              @PathVariable Long id) {
        contactService.deleteContact(id, principal.getUser().getId());
    }
}
