package ru.pratice.pet_project.personal_finance_management_system.controllers.transaction;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pratice.pet_project.personal_finance_management_system.entities.Transaction;
import ru.pratice.pet_project.personal_finance_management_system.services.transactions.TransactionDeleteService;
import ru.pratice.pet_project.personal_finance_management_system.services.transactions.TransactionSaveAndUpdateService;


@RestController
@RequestMapping("api/transactions")
@AllArgsConstructor
public class TransactionDeleteAndSaveController {
    TransactionDeleteService transactionDeleteService;
    TransactionSaveAndUpdateService transactionService;

    @DeleteMapping(path = "/delete/{id}")
    public void deleteTransaction(@PathVariable(name = "id") long id) {
        transactionDeleteService.deleteTransactionById(id);
    }

    @DeleteMapping(path = "/delete/{username}")
    public void deleteTransactionByUsername(@PathVariable(name = "username") String username) {
        transactionDeleteService.deleteTransactionsByUsername(username.trim());
    }

    @DeleteMapping("/delete-by-type")
    public void deleteTransactionByType(@RequestParam String type,
                                        @RequestParam String username) {
        transactionDeleteService.deleteTransactionsByType(type.trim(), username.trim());
    }

    @PostMapping("/save")
    public void createTransaction(@RequestBody Transaction transaction) {
        transactionService.saveTransaction(transaction);
    }
}













































