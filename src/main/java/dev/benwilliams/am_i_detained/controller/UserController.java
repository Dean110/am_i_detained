package dev.benwilliams.am_i_detained.controller;

import dev.benwilliams.am_i_detained.dto.UserDto;
import dev.benwilliams.am_i_detained.security.CustomOAuth2User;
import dev.benwilliams.am_i_detained.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String profile(@AuthenticationPrincipal CustomOAuth2User principal, Model model) {
        UserDto user = userService.getUser(principal.getUser().getId());
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping
    public String updateProfile(@AuthenticationPrincipal CustomOAuth2User principal,
                                @RequestParam String phoneNumber,
                                @RequestParam String alias,
                                @RequestParam String manualLocation,
                                @RequestParam String customAlertMessage) {
        userService.updateProfile(principal.getUser().getId(), phoneNumber, alias, manualLocation, customAlertMessage);
        return "redirect:/profile";
    }
}
