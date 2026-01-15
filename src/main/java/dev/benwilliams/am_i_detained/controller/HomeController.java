package dev.benwilliams.am_i_detained.controller;

import dev.benwilliams.am_i_detained.security.CustomOAuth2User;
import dev.benwilliams.am_i_detained.service.CheckInScheduleService;
import dev.benwilliams.am_i_detained.service.CheckInService;
import dev.benwilliams.am_i_detained.service.ContactService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {
    private final CheckInScheduleService scheduleService;
    private final ContactService contactService;
    private final CheckInService checkInService;

    public HomeController(CheckInScheduleService scheduleService,
                          ContactService contactService,
                          CheckInService checkInService) {
        this.scheduleService = scheduleService;
        this.contactService = contactService;
        this.checkInService = checkInService;
    }
    
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomOAuth2User principal, Model model) {
        Long userId = principal.getUser().getId();
        model.addAttribute("schedule", scheduleService.getSchedule(userId));
        model.addAttribute("contactCount", contactService.getContacts(userId).size());
        model.addAttribute("recentCheckIns", checkInService.getHistory(userId, 7));
        return "dashboard";
    }

    @PostMapping("/dashboard/checkin")
    public String quickCheckIn(@AuthenticationPrincipal CustomOAuth2User principal) {
        checkInService.manualCheckIn(principal.getUser().getId());
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/alert")
    public String quickAlert(@AuthenticationPrincipal CustomOAuth2User principal) {
        checkInService.triggerEmergencyAlert(principal.getUser().getId());
        return "redirect:/dashboard";
    }
}
