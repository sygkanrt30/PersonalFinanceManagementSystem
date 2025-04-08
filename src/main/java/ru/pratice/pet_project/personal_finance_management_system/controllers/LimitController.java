package ru.pratice.pet_project.personal_finance_management_system.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pratice.pet_project.personal_finance_management_system.repositories.limits.LimitTracker;
import ru.pratice.pet_project.personal_finance_management_system.services.limits.LimitService;

@RestController
@RequestMapping("api/limits")
@AllArgsConstructor
public class LimitController {
    LimitService limitService;

    @GetMapping(path = "{id}")
    public LimitTracker getLimitById(@PathVariable long id) {
        return limitService.getLimitById(id);
    }

    @GetMapping("/get_by_username")
    public LimitTracker getLimitByUsername(@RequestParam String username) {
        return limitService.getLimitByUsername(username.trim());
    }

    @PatchMapping("/update_limit_amount")
    public void updateLimitAmount(@RequestParam long id, @RequestParam long limitAmount) {
        limitService.updateLimitAmount(id, limitAmount);
    }
}
