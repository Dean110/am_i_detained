package dev.benwilliams.am_i_detained.controller;

import dev.benwilliams.am_i_detained.entity.Contact;
import dev.benwilliams.am_i_detained.security.CustomOAuth2User;
import dev.benwilliams.am_i_detained.service.ContactService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contacts")
public class ContactController {
    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public String contacts(@AuthenticationPrincipal CustomOAuth2User principal, Model model) {
        model.addAttribute("contacts", contactService.getContacts(principal.getUser().getId()));
        return "contacts";
    }

    @PostMapping
    public String createContact(@AuthenticationPrincipal CustomOAuth2User principal,
                                @RequestParam String alias,
                                @RequestParam String phoneNumber,
                                @RequestParam String email) {
        contactService.createContact(principal.getUser().getId(), alias, phoneNumber, email);
        return "redirect:/contacts";
    }

    @PostMapping("/{id}/delete")
    public String deleteContact(@AuthenticationPrincipal CustomOAuth2User principal,
                                @PathVariable Long id) {
        contactService.deleteContact(id, principal.getUser().getId());
        return "redirect:/contacts";
    }
}
