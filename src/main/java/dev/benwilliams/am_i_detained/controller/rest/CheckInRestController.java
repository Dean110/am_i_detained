package dev.benwilliams.am_i_detained.controller.rest;

import dev.benwilliams.am_i_detained.dto.CheckInHistoryDto;
import dev.benwilliams.am_i_detained.security.CustomOAuth2User;
import dev.benwilliams.am_i_detained.service.CheckInService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkin")
public class CheckInRestController {
    private final CheckInService checkInService;

    public CheckInRestController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping
    public Map<String, String> checkIn(@AuthenticationPrincipal CustomOAuth2User principal) {
        checkInService.manualCheckIn(principal.getUser().getId());
        return Map.of("status", "success", "message", "Checked in successfully");
    }

    @GetMapping("/history")
    public List<CheckInHistoryDto> getHistory(@AuthenticationPrincipal CustomOAuth2User principal) {
        return checkInService.getHistory(principal.getUser().getId(), 30);
    }
}
