package ru.pratice.pet_project.personal_finance_management_system.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pratice.pet_project.personal_finance_management_system.repositories.transactions.Transaction;
import ru.pratice.pet_project.personal_finance_management_system.services.transactions.TransactionService;

import java.time.LocalDate;
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

    @GetMapping(path = "/get_by_username/{username}")
    public List<Transaction> getFilteredTransactionsByUsername(@PathVariable(name = "username") String username) {
        return transactionService.getTransactionsByUsername(username.trim());
    }

    @GetMapping(path = "/get_by_category")
    public List<Transaction> getTransactionsByCategory(@RequestParam Long categoryId,
                                                       @RequestParam String username,
                                                       @RequestParam String type) {
        return transactionService.getTransactionsByCategory(categoryId, username.trim(), type.trim());
    }

    @GetMapping(path = "/get_by_date/{date}")
    public List<Transaction> getTransactionsByDate(@PathVariable(name = "date") LocalDate date) {
        return transactionService.getTransactionsByDate(date);
    }

    @GetMapping("/filtered_by_type")
    public List<Transaction> getFilteredTransactionsByType(@RequestParam String type,
                                                           @RequestParam String username) {
        return transactionService.getTransactionsByType(type.trim(), username.trim());
    }

    @GetMapping("/filtered_by_month")
    public List<Transaction> getFilteredTransactionsByMonth(@RequestParam String username,
                                                            @RequestParam String type,
                                                            @RequestParam(required = false) Short minMonth,
                                                            @RequestParam(required = false) Short maxMonth,
                                                            @RequestParam Integer year) {
        return transactionService.getFilteredByMonthsTransactions(type.trim(), username.trim(), minMonth, maxMonth, year);
    }

    @GetMapping("/filtered_by_amount")
    public List<Transaction> getFilteredByAmountTransactions(@RequestParam String username,
                                                             @RequestParam String type,
                                                             @RequestParam(required = false) Long minAmount,
                                                             @RequestParam Long maxAmount) {
        return transactionService.getFilteredByAmountTransactions(type.trim(), username.trim(), minAmount, maxAmount);
    }

    @GetMapping("/filtered_by_date")
    public List<Transaction> getFilteredByDateTransactions(@RequestParam String username,
                                                           @RequestParam String type,
                                                           @RequestParam LocalDate date) {
        return transactionService.getTransactionsByDate(username.trim(), type.trim(), date);
    }

    @GetMapping("/filtered_by_date_and_category")
    public List<Transaction> getFilteredByDateAndCategoryTransactions(@RequestParam String username,
                                                                      @RequestParam String type,
                                                                      @RequestParam LocalDate date,
                                                                      @RequestParam Long categoryId) {
        return transactionService.getTransactionsByCategoryAndDate(username.trim(), type.trim(), date, categoryId);
    }

    @GetMapping("/filtered_by_month_and_category")
    public List<Transaction> getFilteredTransactionsByMonthAndCategory(@RequestParam String username,
                                                                       @RequestParam String type,
                                                                       @RequestParam(required = false) Short minMonth,
                                                                       @RequestParam(required = false) Short maxMonth,
                                                                       @RequestParam Integer year,
                                                                       @RequestParam Long categoryId) {
        return transactionService.getFilteredByMonthsTransactions(type.trim(),
                username.trim(),
                minMonth,
                maxMonth,
                year,
                categoryId);
    }

    @GetMapping("/sorted_by_amount")
    public List<Transaction> getSortedByAmountTransactions(@RequestParam String username,
                                                           @RequestParam String type,
                                                           @RequestParam(required = false) Boolean isIncreasedSort) {
        return transactionService.getSortedByAmountTransactions(username.trim(), type.trim(), isIncreasedSort);
    }

    @GetMapping("/get_limited_number_of_transactions")
    public List<Transaction> getLimitedNumberOfTransactions(@RequestParam String username,
                                                            @RequestParam String type,
                                                            @RequestParam Integer lowLimit,
                                                            @RequestParam Integer highLimit) {
        return transactionService.getLimitedNumberOfTransactions(username.trim(), type.trim(), lowLimit, highLimit);
    }

    @DeleteMapping(path = "{id}")
    public void deleteTransaction(@PathVariable(name = "id") Long id) {
        transactionService.deleteTransactionById(id);
    }

    @DeleteMapping(path = "/delete_by_username/{username}")
    public void deleteTransactionByUsername(@PathVariable(name = "username") String username) {
        transactionService.deleteTransactionsByUsername(username.trim());
    }

    @DeleteMapping("/delete_by_type")
    public void deleteTransactionByType(@RequestParam String type,
                                        @RequestParam String username) {
        transactionService.deleteTransactionsByType(type.trim(), username.trim());
    }

    @PostMapping("/create")
    public void createTransaction(@RequestBody Transaction transaction) {
        transactionService.saveTransaction(transaction);
    }

    @PutMapping(path = "{id}")
    public void updateTransaction(@PathVariable(name = "id") Long id, @RequestBody Transaction transaction) {
        transactionService.updateTransaction(id, transaction);
    }

    @PatchMapping("/update_amount")
    public void updateAmount(@RequestParam Long id, @RequestParam Long amount) {
        transactionService.updateAmount(id, amount);
    }

    @PatchMapping("/update_date")
    public void updateDate(@RequestParam Long id, @RequestParam LocalDate date) {
        transactionService.updateDate(id, date);
    }

    @PatchMapping("/update_description")
    public void updateDescription(@RequestParam Long id, @RequestParam String description) {
        transactionService.updateDescription(id, description);
    }

    @PatchMapping("/update_category_id")
    public void updateCategoryId(@RequestParam Long id, @RequestParam Long categoryId) {
        transactionService.updateCategory(id, categoryId);
    }
}













































