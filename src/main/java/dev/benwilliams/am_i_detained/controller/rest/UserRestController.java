package dev.benwilliams.am_i_detained.controller.rest;

import dev.benwilliams.am_i_detained.dto.UserDto;
import dev.benwilliams.am_i_detained.security.CustomOAuth2User;
import dev.benwilliams.am_i_detained.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserRestController {
    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public UserDto getProfile(@AuthenticationPrincipal CustomOAuth2User principal) {
        return userService.getUser(principal.getUser().getId());
    }

    @PutMapping("/profile")
    public UserDto updateProfile(@AuthenticationPrincipal CustomOAuth2User principal,
                              @RequestBody Map<String, String> updates) {
        return userService.updateProfile(
            principal.getUser().getId(),
            updates.get("phoneNumber"),
            updates.get("alias"),
            updates.get("manualLocation"),
            updates.get("customAlertMessage")
        );
    }

    @PutMapping("/location")
    public UserDto updateLocation(@AuthenticationPrincipal CustomOAuth2User principal,
                               @RequestBody Map<String, Double> location) {
        return userService.updateLocation(
            principal.getUser().getId(),
            location.get("latitude"),
            location.get("longitude")
        );
    }
}
