package ru.pratice.pet_project.personal_finance_management_system.controllers.transaction;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pratice.pet_project.personal_finance_management_system.entities.Transaction;
import ru.pratice.pet_project.personal_finance_management_system.services.transactions.TransactionSaveAndUpdateService;

import java.time.LocalDate;

@RestController
@RequestMapping("api/transactions_update")
@AllArgsConstructor
public class TransactionUpdateController {
    TransactionSaveAndUpdateService transactionService;

    @PutMapping(path = "/{id}")
    public void updateTransaction(@PathVariable(name = "id") long id, @RequestBody Transaction transaction) {
        transactionService.updateTransaction(id, transaction);
    }

    @PatchMapping("/amount")
    public void updateAmount(@RequestParam long id, @RequestParam long amount) {
        transactionService.updateAmount(id, amount);
    }

    @PatchMapping("/date")
    public void updateDate(@RequestParam long id, @RequestParam LocalDate date) {
        transactionService.updateDate(id, date);
    }

    @PatchMapping("/description")
    public void updateDescription(@RequestParam long id, @RequestParam String description) {
        transactionService.updateDescription(id, description);
    }

    @PatchMapping("/category_id")
    public void updateCategoryId(@RequestParam long id, @RequestParam long categoryId) {
        transactionService.updateCategory(id, categoryId);
    }
}
