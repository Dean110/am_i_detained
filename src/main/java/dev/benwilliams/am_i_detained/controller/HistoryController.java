package dev.benwilliams.am_i_detained.controller;

import dev.benwilliams.am_i_detained.security.CustomOAuth2User;
import dev.benwilliams.am_i_detained.service.CheckInService;
import dev.benwilliams.am_i_detained.repository.AlertHistoryRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/history")
public class HistoryController {
    private final CheckInService checkInService;
    private final AlertHistoryRepository alertHistoryRepository;

    public HistoryController(CheckInService checkInService, AlertHistoryRepository alertHistoryRepository) {
        this.checkInService = checkInService;
        this.alertHistoryRepository = alertHistoryRepository;
    }

    @GetMapping
    public String history(@AuthenticationPrincipal CustomOAuth2User principal, Model model) {
        model.addAttribute("checkIns", checkInService.getHistory(principal.getUser().getId(), 30));
        model.addAttribute("alerts", alertHistoryRepository.findByUserIdAndAlertTimestampAfterOrderByAlertTimestampDesc(
            principal.getUser().getId(),
            LocalDateTime.now().minusDays(30)
        ));
        return "history";
    }
}
