package dev.benwilliams.am_i_detained.controller;

import dev.benwilliams.am_i_detained.security.CustomOAuth2User;
import dev.benwilliams.am_i_detained.service.CheckInScheduleService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

@Controller
@RequestMapping("/schedule")
public class CheckInScheduleController {
    private final CheckInScheduleService scheduleService;

    public CheckInScheduleController(CheckInScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public String schedule(@AuthenticationPrincipal CustomOAuth2User principal, Model model) {
        model.addAttribute("schedule", scheduleService.getSchedule(principal.getUser().getId()));
        return "schedule";
    }

    @PostMapping
    public String updateSchedule(@AuthenticationPrincipal CustomOAuth2User principal,
                                 @RequestParam String checkInTime,
                                 @RequestParam Integer gracePeriodMinutes,
                                 @RequestParam(required = false) Boolean enabled) {
        scheduleService.updateSchedule(
            principal.getUser().getId(),
            LocalTime.parse(checkInTime),
            gracePeriodMinutes,
            enabled != null && enabled
        );
        return "redirect:/schedule";
    }
}
