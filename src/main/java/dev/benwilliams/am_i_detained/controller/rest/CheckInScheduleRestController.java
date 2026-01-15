package dev.benwilliams.am_i_detained.controller.rest;

import dev.benwilliams.am_i_detained.dto.CheckInScheduleDto;
import dev.benwilliams.am_i_detained.security.CustomOAuth2User;
import dev.benwilliams.am_i_detained.service.CheckInScheduleService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.Map;

@RestController
@RequestMapping("/api/checkin/schedule")
public class CheckInScheduleRestController {
    private final CheckInScheduleService scheduleService;

    public CheckInScheduleRestController(CheckInScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public CheckInScheduleDto getSchedule(@AuthenticationPrincipal CustomOAuth2User principal) {
        return scheduleService.getSchedule(principal.getUser().getId());
    }

    @PutMapping
    public CheckInScheduleDto updateSchedule(@AuthenticationPrincipal CustomOAuth2User principal,
                                          @RequestBody Map<String, Object> data) {
        return scheduleService.updateSchedule(
            principal.getUser().getId(),
            LocalTime.parse((String) data.get("checkInTime")),
            (Integer) data.get("gracePeriodMinutes"),
            (Boolean) data.get("enabled")
        );
    }
}
