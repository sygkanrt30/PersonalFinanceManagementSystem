package ru.pratice.pet_project.personal_finance_management_system.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pratice.pet_project.personal_finance_management_system.repositories.transactions.Transaction;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.InvalidEntityException;
import ru.pratice.pet_project.personal_finance_management_system.services.transactions.TransactionService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("api/transactions")
@AllArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping
    public List<Transaction> getTransactions() {
        return transactionService.getTransactions();
    }

    @GetMapping(path = "{id}")
    public Transaction getTransaction(@PathVariable(name = "id") Long id) {
        return transactionService.getTransactionById(id);
    }

    @GetMapping(path = "{owner}")
    public List<Transaction> getTransactionsByOwner(@PathVariable(name = "owner") String owner) {
        return transactionService.getTransactionsByOwner(owner.trim());
    }

    @GetMapping(path = "{type}")
    public List<Transaction> getTransactionsByType(@PathVariable(name = "type") String type) {
        return transactionService.getTransactionsByType(type.trim());
    }

    @GetMapping("/filtered_by_month")
    public List<Transaction> getFilteredTransactionsByMonth(@RequestParam String name,
                                                            @RequestParam String type,
                                                            @RequestParam(required = false) Short minMonth,
                                                            @RequestParam(required = false) Short maxMonth,
                                                            @RequestParam Integer year) {
        return transactionService.getFilteredByMonthsTransactions(type, name, minMonth, maxMonth, year);
    }

    @GetMapping("/filtered_by_amount")
    public List<Transaction> getFilteredByAmountTransactions(@RequestParam String name,
                                                             @RequestParam String type,
                                                             @RequestParam(required = false) Long minAmount,
                                                             @RequestParam Long maxAmount) {
        return transactionService.getFilteredByAmountTransactions(type, name, minAmount, maxAmount);
    }

    @GetMapping(path = "{category_id}")
    public List<Transaction> getTransactionsByCategory(@PathVariable(name = "category_id") Long categoryId) {
        return transactionService.getTransactionsByCategory(categoryId);
    }

    @GetMapping(path = "{date}")
    public List<Transaction> getTransactionsByDate(@PathVariable(name = "date") String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return transactionService.getTransactionsByDate(localDate);
    }

    @DeleteMapping(path = "{id}")
    public void deleteTransaction(@PathVariable(name = "id") Long id) {
        transactionService.deleteTransactionById(id);
    }

    @DeleteMapping(path = "{owner}")
    public void deleteTransactionByOwner(@PathVariable(name = "owner") String owner) {
        transactionService.deleteTransactionsByOwner(owner.trim());
    }

    @DeleteMapping(path = "{type}")
    public void deleteTransactionByType(@PathVariable(name = "type") String type) {
        transactionService.deleteTransactionsByType(type.trim());
    }

    @PostMapping("/create")
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        return transactionService.createTransaction(transaction);
    }

    @PutMapping(path = "{id}")
    public void updateTransaction(@PathVariable(name = "id") Long id, @RequestBody Transaction transaction) {
        transactionService.updateTransaction(id, transaction);
    }

    @PatchMapping(path = "{id}")
    public void updatePartOfTransaction(@PathVariable(name = "id") Long id,
                                        @RequestParam(required = false) Long amount,
                                        @RequestParam(required = false) String date,
                                        @RequestParam(required = false) String description,
                                        @RequestParam(required = false) Long categoryId) {
        int countNotNullParam = 0;
        if (amount != null) {
            countNotNullParam++;
            transactionService.updateAmount(id, amount);
        }
        if (date != null) {
            countNotNullParam++;
            transactionService.updateDate(id, date);
        }
        if (description != null) {
            countNotNullParam++;
            transactionService.updateDescription(id, description);
        }
        if (categoryId != null) {
            countNotNullParam++;
            transactionService.updateCategory(id, categoryId);
        }
        if (countNotNullParam == 0) {
            throw new InvalidEntityException("There are no parameters for changes in the request for changes",
                    LocalTime.now());
        }
    }

}













































