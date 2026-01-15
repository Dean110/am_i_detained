package dev.benwilliams.am_i_detained.controller.rest;

import dev.benwilliams.am_i_detained.dto.AlertHistoryDto;
import dev.benwilliams.am_i_detained.mapper.AlertHistoryMapper;
import dev.benwilliams.am_i_detained.repository.AlertHistoryRepository;
import dev.benwilliams.am_i_detained.security.CustomOAuth2User;
import dev.benwilliams.am_i_detained.service.CheckInService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alert")
public class AlertRestController {
    private final CheckInService checkInService;
    private final AlertHistoryRepository alertHistoryRepository;
    private final AlertHistoryMapper alertHistoryMapper;

    public AlertRestController(CheckInService checkInService, 
                               AlertHistoryRepository alertHistoryRepository,
                               AlertHistoryMapper alertHistoryMapper) {
        this.checkInService = checkInService;
        this.alertHistoryRepository = alertHistoryRepository;
        this.alertHistoryMapper = alertHistoryMapper;
    }

    @PostMapping
    public Map<String, String> triggerAlert(@AuthenticationPrincipal CustomOAuth2User principal) {
        checkInService.triggerEmergencyAlert(principal.getUser().getId());
        return Map.of("status", "success", "message", "Emergency alert sent");
    }

    @GetMapping("/history")
    public List<AlertHistoryDto> getHistory(@AuthenticationPrincipal CustomOAuth2User principal) {
        return alertHistoryMapper.toDtoList(
            alertHistoryRepository.findByUserIdAndAlertTimestampAfterOrderByAlertTimestampDesc(
                principal.getUser().getId(),
                LocalDateTime.now().minusDays(30)
            )
        );
    }
}
