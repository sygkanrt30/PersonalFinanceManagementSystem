package ru.pratice.pet_project.personal_finance_management_system.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pratice.pet_project.personal_finance_management_system.repositories.limits.LimitTracker;
import ru.pratice.pet_project.personal_finance_management_system.services.limits.LimitService;

@RestController
@RequestMapping("api/limits")
@AllArgsConstructor
public class LimitController {
    private final LimitService limitService;

    @GetMapping(path = "{id}")
    public LimitTracker getLimitBy(@PathVariable Long id) {
        return limitService.getLimitById(id);
    }

    @GetMapping("/get_by_username")
    public LimitTracker getLimitByUsername(@RequestParam String username) {
        return limitService.getLimitByUsername(username.trim());
    }

    @DeleteMapping(path = "{id}")
    public void deleteLimit(@PathVariable Long id) {
        limitService.deleteLimitById(id);
    }

    @DeleteMapping("/delete_by_username")
    public void deleteLimitByUsername(@RequestParam String username) {
        limitService.deleteLimitByUsername(username.trim());
    }

    @PostMapping("/create")
    public void createLimit(@RequestBody LimitTracker limit) {
        limitService.saveLimit(limit);
    }

    @PatchMapping("/update_limit_amount")
    public void updateLimitAmount(@RequestParam Long id, @RequestParam Long limitAmount) {
        limitService.updateLimitAmount(id, limitAmount);
    }

    @PatchMapping("/update_total_amount")
    public void updateTotalAmount(@RequestParam Long id, @RequestParam Long totalAmount) {
        limitService.updateTotalAmount(id, totalAmount);
    }
}
